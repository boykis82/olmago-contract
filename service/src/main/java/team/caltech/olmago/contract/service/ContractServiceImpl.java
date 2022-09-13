package team.caltech.olmago.contract.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.contract.*;
import team.caltech.olmago.contract.contract.event.ContractChangeCanceled;
import team.caltech.olmago.contract.contract.event.ContractChanged;
import team.caltech.olmago.contract.contract.event.Event;
import team.caltech.olmago.contract.dto.*;
import team.caltech.olmago.contract.event.EventPublisher;
import team.caltech.olmago.contract.exception.InvalidArgumentException;
import team.caltech.olmago.contract.plm.DiscountPolicy;
import team.caltech.olmago.contract.plm.DiscountPolicyRepository;
import team.caltech.olmago.contract.product.ProductSubscription;
import team.caltech.olmago.contract.product.factory.ProductFactory;
import team.caltech.olmago.contract.product.factory.ProductFactoryMap;
import team.caltech.olmago.contract.proxy.associatedcompany.AssociatedCompanyServiceProxy;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ContractServiceImpl implements ContractService {
  
  private final ProductFactoryMap productFactoryMap;
  private final ContractRepository contractRepository;
  private final DiscountPolicyRepository discountPolicyRepository;
  
  private final PackageService packageService;
  private final AssociatedCompanyServiceProxy associatedCompanyServiceProxy;
  
  private final EventPublisher eventPublisher;
  
  public static final String CONTRACT_EVENT_CHANNEL = "contract-event";
  
  @Override
  @Transactional
  public List<ContractDto> receiveContractSubscription(ReceiveContractSubscriptionDto dto) {
    List<ContractDto> contractDtos = new ArrayList<>();
    List<Event> events = new ArrayList<>();
    // package 할인 조건 중 option에 의존하는게 있으므로 option 먼저 생성
    if (isPackageSubscribing(dto)) {
      Contract optContract = contractRepository.save(
          createContract(dto.getCustomerId(), dto.getOrderId(), dto.getSubRcvDtm(), dto.getOptProdCd(), ContractType.OPTION)
      );
      Contract pkgContract = contractRepository.save(
          createContract(dto.getCustomerId(), dto.getOrderId(), dto.getSubRcvDtm(), dto.getPkgProdCd(), ContractType.PACKAGE)
      );
      events.add(optContract.receiveSubscription());
      events.add(pkgContract.receiveSubscription());
      
      packageService.createPackage(pkgContract, optContract, dto.getSubRcvDtm());
      
      contractDtos.add(ContractDto.of(optContract));
      contractDtos.add(ContractDto.of(pkgContract));
    }
    //-- unit 계약 생성
    List<Contract> unitContracts = contractRepository.saveAll(
        dto.getUnitProdCds().stream()
            .map(prodCd -> createContract(dto.getCustomerId(), dto.getOrderId(), dto.getSubRcvDtm(), prodCd, ContractType.UNIT))
            .collect(Collectors.toList())
    );
    contractDtos.addAll(unitContracts.stream()
        .map(ContractDto::of)
        .collect(Collectors.toList())
    );
    events.addAll(unitContracts.stream().map(Contract::receiveSubscription).collect(Collectors.toList()));
    
    eventPublisher.fire(CONTRACT_EVENT_CHANNEL, events);
    
    return contractDtos;
  }

  private boolean isPackageSubscribing(ReceiveContractSubscriptionDto dto) {
    return !(dto.getPkgProdCd().isEmpty() || !dto.getOptProdCd().isEmpty());
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
    eventPublisher.fire(
        CONTRACT_EVENT_CHANNEL,
        contract.completeSubscription(dto.getSubscriptionCompletedDateTime())
    );

    if (contract.getContractType() != ContractType.UNIT) {
      packageService.completePackageSubscription(contract, dto.getSubscriptionCompletedDateTime());
    }
    sendSubscriptionToAssociatedCompany(contract, dto.getSubscriptionCompletedDateTime());
    
    return ContractDto.of(contract);
  }
  
  private void sendSubscriptionToAssociatedCompany(Contract contract, LocalDateTime dtm) {
    associatedCompanyServiceProxy.subscribe(
        contract.getId(),
        contract.getProductSubscriptions()
            .stream()
            .filter(ps -> ps.getLifeCycle().isSubscriptionCompleted())
            .map(ProductSubscription::getProductCode)
            .collect(Collectors.toList()),
        dtm
    );
  }
  
  @Override
  @Transactional
  public ContractDto completeRegularPayment(CompleteRegularPaymentDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId())
        .orElseThrow(InvalidArgumentException::new);
    contract.completeRegularPayment(dto.getRegularPaymentCompletedDateTime());
  
    sendSubscriptionToAssociatedCompany(contract, dto.getRegularPaymentCompletedDateTime());
  
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public List<ContractDto> receiveContractTermination(ReceiveContractTerminationDto dto) {
    List<ContractDto> contractDtos = new ArrayList<>();
    List<Event> events = new ArrayList<>();
    
    if (dto.includePackage()) {
      Contract pkgContract = contractRepository.findById(dto.getPackageContractId())
          .orElseThrow(InvalidArgumentException::new);
      events.add(pkgContract.receiveTermination(dto.getOrderId(), dto.getTerminationReceivedDateTime()));

      Contract optContract = contractRepository.findById(dto.getOptionContractId())
          .orElseThrow(InvalidArgumentException::new);
      events.add(optContract.receiveTermination(dto.getOrderId(), dto.getTerminationReceivedDateTime()));

      packageService.receiveTermination(pkgContract, optContract, dto.getTerminationReceivedDateTime());
      
      contractDtos.add(ContractDto.of(pkgContract));
      contractDtos.add(ContractDto.of(optContract));
    }
    List<Contract> contracts = contractRepository.findAllById(dto.getUnitContractIds());
    contracts.forEach(c -> events.add(c.receiveTermination(dto.getOrderId(), dto.getTerminationReceivedDateTime())));
    
    contractDtos.addAll(contracts.stream()
        .map(ContractDto::of)
        .collect(Collectors.toList())
    );
  
    eventPublisher.fire(CONTRACT_EVENT_CHANNEL, events);
    
    return contractDtos;
  }
  
  @Override
  public List<ContractDto> cancelContractTerminationReceipt(CancelContractTerminationDto dto) {
    List<ContractDto> contractDtos = new ArrayList<>();
    List<Event> events = new ArrayList<>();
    
    if (dto.includePackage()) {
      Contract pkgContract = contractRepository.findById(dto.getPackageContractId())
          .orElseThrow(InvalidArgumentException::new);
      events.add(pkgContract.cancelTerminationReceipt(dto.getOrderId(), dto.getCancelTerminationReceiptDateTime()));

      Contract optContract = contractRepository.findById(dto.getOptionContractId())
          .orElseThrow(InvalidArgumentException::new);
      events.add(optContract.cancelTerminationReceipt(dto.getOrderId(), dto.getCancelTerminationReceiptDateTime()));

      packageService.cancelTerminationReceipt(pkgContract, optContract, dto.getCancelTerminationReceiptDateTime());
  
      contractDtos.add(ContractDto.of(pkgContract));
      contractDtos.add(ContractDto.of(optContract));
    }
    List<Contract> contracts = contractRepository.findAllById(dto.getUnitContractIds());
    contracts.forEach(c -> events.add(c.cancelTerminationReceipt(dto.getOrderId(), dto.getCancelTerminationReceiptDateTime())));
  
    contractDtos.addAll(contracts.stream()
        .map(ContractDto::of)
        .collect(Collectors.toList())
    );
  
    eventPublisher.fire(CONTRACT_EVENT_CHANNEL, events);
    
    return contractDtos;
  }
  
  @Override
  @Transactional
  public ContractDto completeContractTermination(CompleteContractTerminationDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId())
        .orElseThrow(InvalidArgumentException::new);
    eventPublisher.fire(
        CONTRACT_EVENT_CHANNEL,
        contract.completeTermination(dto.getTerminationCompletedDateTime())
    );
    
    if (contract.getContractType() != ContractType.UNIT) {
      packageService.completePackageTermination(contract, dto.getTerminationCompletedDateTime());
    }
    sendTerminationToAssociatedCompany(contract, dto.getTerminationCompletedDateTime());
    
    return ContractDto.of(contract);
  }
  
  private void sendTerminationToAssociatedCompany(Contract contract, LocalDateTime dtm) {
    associatedCompanyServiceProxy.terminate(
        contract.getId(),
        contract.getProductSubscriptions()
            .stream()
            .map(ProductSubscription::getProductCode)
            .collect(Collectors.toList()),
        dtm
    );
  }
  
  @Override
  @Transactional
  public List<ContractDto> receiveContractChange(ReceiveContractChangeDto dto) {
    List<ContractDto> contractDtos = new ArrayList<>();
    if (dto.isChangingBothContract()) {
      contractDtos.addAll(
          changeBothContract(dto).stream()
              .map(ContractDto::of)
              .collect(Collectors.toList())
      );
    }
    else {
      if (dto.isChangingPackageContract()) {
        contractDtos.add(
            ContractDto.of(changePackageContract(dto, false))
        );
      }
      else if (dto.isChangingOptionContract()) {
        contractDtos.addAll(
          changeOptionContract(dto, false).stream()
              .map(ContractDto::of)
              .collect(Collectors.toList())
        );
      }
      else {
        throw new InvalidArgumentException();
      }
    }
    eventPublisher.fire(
        CONTRACT_EVENT_CHANNEL,
        contractDtos.stream()
            .map(c -> new ContractChanged(c.getContractId(), dto.getOrderId(), dto.getChangeReceivedDateTime()))
            .collect(Collectors.toList())
    );
    return contractDtos;
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

    Contract pkgContract = contractRepository.findById(dto.getPackageContractId())
        .orElseThrow(InvalidArgumentException::new);

    ProductFactory bfPkgProdFactory = productFactoryMap.get(dto.getBeforePackageProductCode());
    ProductFactory afPkgProdFactory = productFactoryMap.get(dto.getAfterPackageProductCode());

    List<String> termProdCodes = bfPkgProdFactory.getShouldBeTerminatedProductCodes(afPkgProdFactory);
    List<ProductSubscription> newBasicBenefitProductSubscriptions
        = afPkgProdFactory.receiveSubscription(pkgContract, dto.getChangeReceivedDateTime(), bfPkgProdFactory.getBasicBenefitProductCodes());

    pkgContract.changeContract(dto.getOrderId(), dto.getAfterPackageProductCode(), termProdCodes, newBasicBenefitProductSubscriptions, dto.getChangeReceivedDateTime());
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
    
    Contract afOptContract = createContract(dto.getCustomerId(), dto.getOrderId(), dto.getChangeReceivedDateTime(), dto.getAfterOptionProductCode(), ContractType.OPTION);
    afOptContract.setBillCycle(pkgContract.copyBillCycle());
    contractRepository.save(afOptContract);

    if (dto.isKeepingBeforeContract()) {
      bfOptContract.changeToUnit(dto.getOrderId(), dto.getChangeReceivedDateTime());
    }
    else {
      bfOptContract.receiveTermination(dto.getOrderId(), dto.getChangeReceivedDateTime());
    }
    packageService.changePackageComposition(pkgContract, bfOptContract, afOptContract, dto.getChangeReceivedDateTime());
    
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
    contracts.forEach(c -> c.cancelContractChange(dto.getOrderId(), dto.getCanceledChangeReceiptDateTime()));
    eventPublisher.fire(
        CONTRACT_EVENT_CHANNEL,
        contracts.stream()
            .map(c -> new ContractChangeCanceled(c.getId(), dto.getOrderId(), dto.getCanceledChangeReceiptDateTime()))
            .collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }
  
  @Override
  @Transactional
  public ContractDto receiveCouponDiscount(ReceiveCouponDiscountDto dto) {
    DiscountPolicy discountPolicy = discountPolicyRepository.findByCouponPolicyCode(dto.getCouponPolicyCode())
            .orElseThrow(InvalidArgumentException::new);
    Contract contract = contractRepository.findById(dto.getContractId())
        .orElseThrow(InvalidArgumentException::new);
    
    contract.receiveCouponDiscount(discountPolicy, dto.getCouponId(), dto.getCouponUseReservedDateTime());
    
    return ContractDto.of(contract);
  }
  
}
