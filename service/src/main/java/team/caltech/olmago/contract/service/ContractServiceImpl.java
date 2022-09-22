package team.caltech.olmago.contract.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.contract.ContractType;
import team.caltech.olmago.contract.contract.event.ContractEventBase;
import team.caltech.olmago.contract.contract.event.DiscountChanged;
import team.caltech.olmago.contract.dto.*;
import team.caltech.olmago.contract.message.MessageEnvelope;
import team.caltech.olmago.contract.message.MessageEnvelopeRepository;
import team.caltech.olmago.contract.exception.InvalidArgumentException;
import team.caltech.olmago.contract.plm.DiscountPolicy;
import team.caltech.olmago.contract.plm.DiscountPolicyRepository;
import team.caltech.olmago.contract.product.ProductSubscription;
import team.caltech.olmago.contract.product.factory.ProductFactory;
import team.caltech.olmago.contract.product.factory.ProductFactoryMap;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static team.caltech.olmago.contract.plm.DiscountType.MOBILE_PHONE_PRICE_PLAN_LINKED;

@RequiredArgsConstructor
@Service
public class ContractServiceImpl implements ContractService {
  
  private final ProductFactoryMap productFactoryMap;
  private final ContractRepository contractRepository;
  private final DiscountPolicyRepository discountPolicyRepository;
  
  private final PackageService packageService;

  private final MessageEnvelopeRepository messageEnvelopeRepository;
  
  public static final String CONTRACT_AGGREGATE_TYPE = "CONTRACT";
  public static final String CONTRACT_EVENT_BINDING = "contract-event-0";
  
  @Override
  @Transactional
  public List<ContractDto> receiveContractSubscription(ReceiveContractSubscriptionDto dto) {
    List<Contract> contracts = new ArrayList<>();
    
    if (isPackageSubscribing(dto)) {
      //-- 패키지 계약 생성
      contracts.addAll(receivePackageContractSubscription(dto));
    }
    //-- unit 계약 생성
    contracts.addAll(receiveOptionContractSubscription(dto));
  
    //-- 이벤트 보관
    messageEnvelopeRepository.saveAll(
        contracts.stream().map(c -> wrapEvent(c.receiveSubscription())).collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }
  
  private boolean isPackageSubscribing(ReceiveContractSubscriptionDto dto) {
    return !(dto.getPkgProdCd().isEmpty() || !dto.getOptProdCd().isEmpty());
  }
  
  private List<Contract> receivePackageContractSubscription(ReceiveContractSubscriptionDto dto) {
    // package 할인 조건 중 option에 의존하는게 있으므로 option 먼저 생성
    Contract optContract = contractRepository.save(
        createContract(dto.getCustomerId(), dto.getOrderId(), dto.getSubRcvDtm(), dto.getOptProdCd(), ContractType.OPTION)
    );
    Contract pkgContract = contractRepository.save(
        createContract(dto.getCustomerId(), dto.getOrderId(), dto.getSubRcvDtm(), dto.getPkgProdCd(), ContractType.PACKAGE)
    );
    packageService.createPackage(pkgContract, optContract, dto.getSubRcvDtm());
    return List.of(optContract, pkgContract);
  }
  
  private List<Contract> receiveOptionContractSubscription(ReceiveContractSubscriptionDto dto) {
    return contractRepository.saveAll(
        dto.getUnitProdCds().stream()
            .map(prodCd -> createContract(dto.getCustomerId(), dto.getOrderId(), dto.getSubRcvDtm(), prodCd, ContractType.UNIT))
            .collect(Collectors.toList())
    );
  }
  
  private Contract createContract(long customerId,
                                  long orderId,
                                  LocalDateTime subRcvDtm,
                                  String productCode,
                                  ContractType contractType
  ) {
    Contract contract = Contract.builder()
        .customerId(customerId)
        .orderId(orderId)
        .subRcvDtm(subRcvDtm)
        .contractType(contractType)
        .feeProductCode(productCode)
        .build();

    List<ProductSubscription> productSubscriptions =
        productFactoryMap.get(productCode).receiveSubscription(contract, subRcvDtm);
    contract.addProductSubscriptions(productSubscriptions);
    return contract;
  }

  @Override
  @Transactional
  public ContractDto completeContractSubscription(CompleteContractSubscriptionDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    if (contract.getContractType() != ContractType.UNIT) {
      packageService.completePackageSubscription(contract, dto.getSubscriptionCompletedDateTime());
    }
    // 이벤트 보관
    messageEnvelopeRepository.save(
        wrapEvent(contract.completeSubscription(dto.getSubscriptionCompletedDateTime()))
    );
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public ContractDto cancelContractSubscriptionReceipt(CancelContractSubscriptionDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    if (contract.getContractType() != ContractType.UNIT) {
      packageService.cancelPackageSubscriptionReceipt(contract, dto.getSubscriptionCanceledDateTime());
    }
    // 이벤트 보관
    messageEnvelopeRepository.save(
        wrapEvent(contract.cancelSubscriptionReceipt(dto.getSubscriptionCanceledDateTime()))
    );
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public ContractDto activateOrDeactivateProducts(ActivateOrDeactivateProductDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    // 이벤트 보관
    messageEnvelopeRepository.save(
        wrapEvent(contract.activateOrDeactivateProducts(dto.getRegularPaymentCompletedDateTime()))
    );
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public ContractDto holdActivation(HoldActivationDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    contract.holdProductActivations(dto.getRegularPaymentCanceledDateTime());
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public List<ContractDto> receiveContractTermination(ReceiveContractTerminationDto dto) {
    List<Contract> contracts = new ArrayList<>();
    if (dto.includePackage()) {
      Contract pkgContract = contractRepository.findById(dto.getPackageContractId()).orElseThrow(InvalidArgumentException::new);
      Contract optContract = contractRepository.findById(dto.getOptionContractId()).orElseThrow(InvalidArgumentException::new);
      packageService.receiveTermination(pkgContract, optContract, dto.getTerminationReceivedDateTime());
      
      contracts.add(pkgContract);
      contracts.add(optContract);
    }
    contracts.addAll(contractRepository.findAllById(dto.getUnitContractIds()));
  
    // 이벤트 보관
    messageEnvelopeRepository.saveAll(
        contracts.stream()
            .map(c -> wrapEvent(c.receiveTermination(dto.getOrderId(), dto.getTerminationReceivedDateTime())))
            .collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }
  
  @Override
  @Transactional
  public List<ContractDto> cancelContractTerminationReceipt(CancelContractTerminationDto dto) {
    List<Contract> contracts = new ArrayList<>();
    if (dto.includePackage()) {
      Contract pkgContract = contractRepository.findById(dto.getPackageContractId()).orElseThrow(InvalidArgumentException::new);
      Contract optContract = contractRepository.findById(dto.getOptionContractId()).orElseThrow(InvalidArgumentException::new);
      packageService.cancelTerminationReceipt(pkgContract, optContract, dto.getCancelTerminationReceiptDateTime());
  
      contracts.add(pkgContract);
      contracts.add(optContract);
    }
    contracts.addAll(contractRepository.findAllById(dto.getUnitContractIds()));
    
    // 이벤트 보관
    messageEnvelopeRepository.saveAll(
        contracts.stream()
            .map(c -> wrapEvent(c.cancelTerminationReceipt(dto.getOrderId(), dto.getCancelTerminationReceiptDateTime())))
            .collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }
  
  @Override
  @Transactional
  public ContractDto completeContractTermination(CompleteContractTerminationDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    if (contract.getContractType() != ContractType.UNIT) {
      packageService.completePackageTermination(contract, dto.getTerminationCompletedDateTime());
    }
    // 이벤트 보관
    messageEnvelopeRepository.save(
        wrapEvent(contract.completeTermination(dto.getTerminationCompletedDateTime()))
    );
    return ContractDto.of(contract);
  }

  @Override
  @Transactional
  public List<ContractDto> receiveContractChange(ReceiveContractChangeDto dto) {
    List<Contract> contracts = new ArrayList<>();
    if (dto.isChangingBothContract()) {
      contracts.addAll(changeBothContract(dto));
    }
    else {
      if (dto.isChangingPackageContract()) {
        contracts.add(changePackageContract(dto, false));
      }
      else if (dto.isChangingOptionContract()) {
        contracts.addAll(changeOptionContract(dto, false));
      }
      else {
        throw new InvalidArgumentException();
      }
    }
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }

  private List<Contract> changeBothContract(ReceiveContractChangeDto dto) {
    return Stream.concat(
        Stream.of(changePackageContract(dto, true)),
        changeOptionContract(dto, true).stream()
    ).collect(Collectors.toList());
  }

  /*
  ex) all+배민 -> life+배민
     0) after 패키지가 명의에 존재하면 오류 -> ok
     1) 패키지 상품 변경
       - after 패키지에 붙일 수 없는 옵션이면 오류 -> ok
       - before 자동가입 상품에는 있고 after자동가입상품에는 없는 건 -> 해지
       - after 자동가입 상품에는 있고 before자동가입상품에는 없는 건 -> 가입

     2) package 그룹은 don't touch
 */
  private Contract changePackageContract(ReceiveContractChangeDto dto, boolean optionChangeToo) {
    validateExistingProduct(dto, dto.getAfterPackageProductCode());
    validatePackageOptionRelation(
        dto.getAfterPackageProductCode(),
        optionChangeToo ? dto.getAfterOptionProductCode() : dto.getBeforeOptionProductCode()
    );

    Contract pkgContract = contractRepository.findById(dto.getPackageContractId()).orElseThrow(InvalidArgumentException::new);

    ProductFactory bfPkgProdFactory = productFactoryMap.get(dto.getBeforePackageProductCode());
    ProductFactory afPkgProdFactory = productFactoryMap.get(dto.getAfterPackageProductCode());

    List<String> termProdCodes = bfPkgProdFactory.getShouldBeTerminatedProductCodes(afPkgProdFactory);
    List<ProductSubscription> newBasicBenefitProdSubs
        = afPkgProdFactory.receiveSubscription(pkgContract, dto.getChangeReceivedDateTime(), bfPkgProdFactory.getBasicBenefitProductCodes());
  
    // 이벤트 보관
    messageEnvelopeRepository.save(
        wrapEvent(pkgContract.changeContract(dto.getOrderId(), dto.getAfterPackageProductCode(), termProdCodes, newBasicBenefitProdSubs, dto.getChangeReceivedDateTime()))
    );
    return pkgContract;
  }

  /*
. 옵션 변경(기존 옵션유지여부=N)
  ex) all+배민 -> all+굽네

  0) 패키지에 붙일 수 없는 옵션이면 오류
  1) after옵션(굽네)가 명의에 존재하면 오류
  2) before 옵션 계약 해지예약
  3) after 옵션 계약 신규 생성 (bill cycle은 package에 맞춤)
  4) package 그룹 변경 (배민은 끊고 굽네 연결)

 3. 옵션 변경(기존 옵션유지여부=Y)
  ex) all+배민 -> all+굽네 & 배민

  0) 패키지에 붙일 수 없는 옵션이면 오류
  1) after옵션(굽네)가 명의에 존재하면 오류
  2) before 옵션 계약을 단품으로 전환
  3) after 옵션 계약 신규 생성 (bill cycle은 package에 맞춤)
  4) package 그룹 변경 (배민은 끊고 굽네 연결)
 */
  private List<Contract> changeOptionContract(ReceiveContractChangeDto dto, boolean packageChangeToo) {
    validateExistingProduct(dto, dto.getAfterOptionProductCode());
    validatePackageOptionRelation(
        packageChangeToo ? dto.getAfterPackageProductCode() : dto.getBeforePackageProductCode(),
        dto.getAfterOptionProductCode()
    );

    Contract pkgContract = contractRepository.findById(dto.getPackageContractId()).orElseThrow(InvalidArgumentException::new);
    Contract bfOptContract = contractRepository.findById(dto.getOptionContractId()).orElseThrow(InvalidArgumentException::new);
    
    List<ContractEventBase> events = new ArrayList<>();
    
    // 이후계약 처리
    Contract afOptContract = createContract(dto.getCustomerId(), dto.getOrderId(), dto.getChangeReceivedDateTime(), dto.getAfterOptionProductCode(), ContractType.OPTION);
    afOptContract.setBillCycle(pkgContract.copyBillCycle());
    contractRepository.save(afOptContract);
    events.add(afOptContract.receiveSubscription());
    
    // 이전계약 처리
    if (dto.isKeepingBeforeContract()) {
      events.add(bfOptContract.changeToUnit(dto.getOrderId(), dto.getChangeReceivedDateTime()));
    }
    else {
      events.add(bfOptContract.receiveTermination(dto.getOrderId(), dto.getChangeReceivedDateTime()));
    }
    packageService.changePackageComposition(pkgContract, bfOptContract, afOptContract, dto.getChangeReceivedDateTime());
  
    // 이벤트 보관
    messageEnvelopeRepository.saveAll(
        events.stream().map(this::wrapEvent).collect(Collectors.toList())
    );
    return List.of(bfOptContract, afOptContract);
  }

  private void validateExistingProduct(ReceiveContractChangeDto dto, String afterProductCode) {
    Long sameProductExistedCount = contractRepository.countActiveContractByCustomerAndFeeProductCode(dto.getCustomerId(), afterProductCode);
    if (sameProductExistedCount > 0) {
      throw new InvalidArgumentException();
    }
  }

  private void validatePackageOptionRelation(String packageProductCode, String optionProductCode) {
    ProductFactory pkgProdFactory = productFactoryMap.get(packageProductCode);
    if (!pkgProdFactory.isAvailableOptionProduct(optionProductCode)) {
      throw new InvalidArgumentException();
    }
  }
  
  @Override
  @Transactional
  public List<ContractDto> cancelContractChangeReceipt(CancelContractChangeDto dto) {
    List<Contract> contracts = contractRepository.findByCustomerAndOrderId(dto.getCustomerId(), dto.getOrderId());
    messageEnvelopeRepository.saveAll(
        contracts.stream()
            .map(c -> wrapEvent(c.cancelContractChange(dto.getOrderId(), dto.getCanceledChangeReceiptDateTime())))
            .collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }
  
  @Override
  @Transactional
  public ContractDto receiveCouponDiscount(ReceiveCouponDiscountDto dto) {
    DiscountPolicy discountPolicy = discountPolicyRepository.findByCouponPolicyCode(dto.getCouponPolicyCode()).orElseThrow(InvalidArgumentException::new);
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    messageEnvelopeRepository.save(
        wrapEvent(contract.receiveCouponDiscount(discountPolicy, dto.getCouponId(), dto.getCouponUseReservedDateTime()))
    );
    return ContractDto.of(contract);
  }

  @Override
  @Transactional
  public ContractDto releaseCouponDiscount(ReleaseCouponDiscountDto dto) {
    DiscountPolicy discountPolicy = discountPolicyRepository.findByCouponPolicyCode(dto.getCouponPolicyCode()).orElseThrow(InvalidArgumentException::new);
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    messageEnvelopeRepository.save(
        wrapEvent(contract.releaseCouponDiscount(discountPolicy, dto.getCouponId(), dto.getCouponUseReleasedDateTime()))
    );
    return ContractDto.of(contract);
  }

  @Override
  @Transactional
  public List<ContractDto> changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto dto) {
    List<Contract> contracts = contractRepository.findByCustomerId(dto.getCustomerId(), false);
    messageEnvelopeRepository.saveAll(
        contracts.stream()
            .map(c -> wrapEvent(changeMobilePhoneRelatedDiscount(c, dto.getChangeDateTime())))
            .collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }

  private DiscountChanged changeMobilePhoneRelatedDiscount(Contract contract, LocalDateTime changeDateTime) {
    ProductFactory pf = productFactoryMap.get(contract.getFeeProductCode());
    List<DiscountPolicy> satisfiedMblPhoneLinkedDiscountPolicies =
        pf.satisfiedDiscountPolicies(contract, MOBILE_PHONE_PRICE_PLAN_LINKED);
    return contract.changeMobilePhoneLinkedDiscount(satisfiedMblPhoneLinkedDiscountPolicies, changeDateTime);
  }
  
  private MessageEnvelope wrapEvent(ContractEventBase e) {
    try {
      return MessageEnvelope.wrap(
          CONTRACT_AGGREGATE_TYPE,
          String.valueOf(e.getContractId()),
          CONTRACT_EVENT_BINDING,
          e.getClass().getSimpleName(),
          e
      );
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public List<ContractDto> findByCustomerId(long customerId, boolean includeTerminatedContract) {
    return contractRepository.findByCustomerId(customerId, includeTerminatedContract)
        .stream()
        .map(ContractDto::of)
        .collect(Collectors.toList());
  }

  public List<ContractDto> findByContractId(long contractId, boolean withPackageOrOption) {
    return contractRepository.findByContractId(contractId, withPackageOrOption)
        .stream()
        .map(ContractDto::of)
        .collect(Collectors.toList());
  }
}
