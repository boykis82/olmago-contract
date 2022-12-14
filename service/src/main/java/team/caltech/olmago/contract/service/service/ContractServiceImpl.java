package team.caltech.olmago.contract.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.domain.contract.CalculationResult;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.contract.ContractType;
import team.caltech.olmago.contract.domain.contract.event.ContractEventBase;
import team.caltech.olmago.contract.domain.contract.event.DiscountChanged;
import team.caltech.olmago.contract.service.dto.*;
import team.caltech.olmago.common.message.MessageEnvelope;
import team.caltech.olmago.contract.domain.exception.InvalidArgumentException;
import team.caltech.olmago.contract.service.message.in.command.order.*;
import team.caltech.olmago.contract.service.message.out.MessageStore;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.product.ProductSubscription;
import team.caltech.olmago.contract.domain.product.factory.ProductFactory;
import team.caltech.olmago.contract.domain.product.factory.ProductFactoryMap;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static team.caltech.olmago.contract.domain.plm.discount.DiscountType.MOBILE_PHONE_PRICE_PLAN_LINKED;

@RequiredArgsConstructor
@Service
public class ContractServiceImpl implements ContractService {
  
  private final ProductFactoryMap productFactoryMap;
  private final ContractRepository contractRepository;
  private final DiscountPolicyRepository discountPolicyRepository;
  
  private final PackageService packageService;
  private final MessageStore messageStore;
  private final ObjectMapper objectMapper;
  
  public static final String CONTRACT_AGGREGATE_TYPE = "CONTRACT";
  public static final String CONTRACT_EVENT_BINDING = "contract-event-0";
  
  @Override
  @Transactional
  public List<ContractDto> receiveContractSubscription(ReceiveContractSubscriptionCmd cmd) {
    List<Contract> contracts = new ArrayList<>();
    if (cmd.isPackageSubscribing()) {
      //-- ????????? ?????? ??????
      contracts.addAll(receivePackageContractSubscription(cmd));
    }
    if (cmd.isUnitSubscribing()) {
      //-- unit ?????? ??????
      contracts.addAll(receiveOptionContractSubscription(cmd));
    }
    messageStore.saveMessage(
        contracts.stream().map(c -> wrapEvent(c.receiveSubscription())).collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }
  
  private List<Contract> receivePackageContractSubscription(ReceiveContractSubscriptionCmd cmd) {
    // package ?????? ?????? ??? option??? ??????????????? ???????????? option ?????? ??????
    Contract optContract = contractRepository.save(
        createContract(cmd.getCustomerId(), cmd.getOrderId(), cmd.getSubRcvDtm(), cmd.getOptProdCd(), ContractType.OPTION)
    );
    Contract pkgContract = contractRepository.save(
        createContract(cmd.getCustomerId(), cmd.getOrderId(), cmd.getSubRcvDtm(), cmd.getPkgProdCd(), ContractType.PACKAGE)
    );
    packageService.createPackage(pkgContract, optContract, cmd.getSubRcvDtm(), cmd.getOrderId());
  
    subscribeProducts(optContract, cmd.getSubRcvDtm());
    subscribeProducts(pkgContract, cmd.getSubRcvDtm());
    contractRepository.save(optContract);
    contractRepository.save(pkgContract);
  
    return List.of(optContract, pkgContract);
  }

  private List<Contract> receiveOptionContractSubscription(ReceiveContractSubscriptionCmd cmd) {
    List<Contract> contracts = cmd.getUnitProds().stream()
        .map(prod -> createContract(cmd.getCustomerId(), cmd.getOrderId(), cmd.getSubRcvDtm(), prod.getProdCd(), ContractType.UNIT))
        .collect(Collectors.toList());
    contracts.forEach(c -> {
      subscribeProducts(c, cmd.getSubRcvDtm());
      contractRepository.save(c);
    });
    
    return contractRepository.saveAll(contracts);
  }
  
  private Contract createContract(long customerId,
                                  long orderId,
                                  LocalDateTime subRcvDtm,
                                  String productCode,
                                  ContractType contractType
  ) {
    return Contract.builder()
        .customerId(customerId)
        .orderId(orderId)
        .subRcvDtm(subRcvDtm)
        .contractType(contractType)
        .feeProductCode(productCode)
        .build();
  }
  
  private void subscribeProducts(Contract contract, LocalDateTime subRcvDtm) {
    ProductFactory pf = productFactoryMap.get(contract.getFeeProductCode());
    List<ProductSubscription> productSubscriptions = pf.receiveSubscription(contract, subRcvDtm);
    contract.addProductSubscriptions(productSubscriptions);
  }
  
  @Override
  @Transactional
  public ContractDto completeContractSubscription(CompleteContractSubscriptionDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    if (contract.getContractType() != ContractType.UNIT) {
      packageService.completePackageSubscription(contract, dto.getSubscriptionCompletedDateTime());
    }
    messageStore.saveMessage(
        wrapEvent(contract.completeSubscription(dto.getSubscriptionCompletedDateTime()))
    );
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public ContractDto cancelContractSubscriptionReceipt(CancelContractSubscriptionDto cmd) {
    Contract contract = contractRepository.findById(cmd.getContractId()).orElseThrow(InvalidArgumentException::new);
    if (contract.getContractType() != ContractType.UNIT) {
      packageService.cancelPackageSubscriptionReceipt(contract, cmd.getSubscriptionCanceledDateTime());
    }
    messageStore.saveMessage(
        wrapEvent(contract.cancelSubscriptionReceipt(cmd.getSubscriptionCanceledDateTime()))
    );
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public ContractDto activateOrDeactivateProducts(ActivateOrDeactivateProductDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    messageStore.saveMessage(
        wrapEvent(contract.activateOrDeactivateProducts(dto.getRegularPaymentCompletedDateTime()))
    );
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public ContractDto holdActivation(HoldActivationDto dto) {
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    messageStore.saveMessage(
        wrapEvent(contract.holdProductActivations(dto.getRegularPaymentCanceledDateTime()))
    );
    return ContractDto.of(contract);
  }
  
  @Override
  @Transactional
  public List<ContractDto> receiveContractTermination(ReceiveContractTerminationCmd cmd) {
    List<Contract> contracts = new ArrayList<>();
    if (cmd.includePackage()) {
      Contract pkgContract = contractRepository.findById(cmd.getPackageContractId()).orElseThrow(InvalidArgumentException::new);
      Contract optContract = contractRepository.findById(cmd.getOptionContractId()).orElseThrow(InvalidArgumentException::new);
      packageService.receiveTermination(pkgContract, optContract, cmd.getTerminationReceivedDateTime(), cmd.getOrderId());
      
      contracts.add(pkgContract);
      contracts.add(optContract);
    }
    contracts.addAll(contractRepository.findAllById(cmd.getUnitContractIds()));
  
    messageStore.saveMessage(
        contracts.stream()
            .map(c -> wrapEvent(c.receiveTermination(cmd.getOrderId(), cmd.getTerminationReceivedDateTime())))
            .collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }
  
  @Override
  @Transactional
  public List<ContractDto> cancelContractTerminationReceipt(CancelContractTerminationCmd cmd) {
    List<Contract> contracts = contractRepository.findByLastOrderId(cmd.getOrderId());
    packageService.cancelTerminationReceipt(cmd.getCancelTerminationReceiptDateTime(), cmd.getOrderId());
    messageStore.saveMessage(
        contracts.stream()
            .map(c -> wrapEvent(c.cancelTerminationReceipt(cmd.getOrderId(), cmd.getCancelTerminationReceiptDateTime())))
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
  
    messageStore.saveMessage(
        wrapEvent(contract.completeTermination(dto.getTerminationCompletedDateTime()))
    );
    return ContractDto.of(contract);
  }

  @Override
  @Transactional
  public List<ContractDto> receiveContractChange(ReceiveContractChangeCmd cmd) {
    List<Contract> contracts = new ArrayList<>();
    if (cmd.isChangingBothContract()) {
      contracts.addAll(changeBothContract(cmd));
    }
    else {
      if (cmd.isChangingPackageContract()) {
        contracts.add(changePackageContract(cmd, false));
      }
      else if (cmd.isChangingOptionContract()) {
        contracts.addAll(changeOptionContract(cmd, false));
      }
      else {
        throw new InvalidArgumentException();
      }
    }
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }

  private List<Contract> changeBothContract(ReceiveContractChangeCmd cmd) {
    return Stream.concat(
        Stream.of(changePackageContract(cmd, true)),
        changeOptionContract(cmd, true).stream()
    ).collect(Collectors.toList());
  }

  /*
  ex) all+?????? -> life+??????
     0) after ???????????? ????????? ???????????? ?????? -> ok
     1) ????????? ?????? ??????
       - after ???????????? ?????? ??? ?????? ???????????? ?????? -> ok
       - before ???????????? ???????????? ?????? after???????????????????????? ?????? ??? -> ??????
       - after ???????????? ???????????? ?????? before???????????????????????? ?????? ??? -> ??????

     2) package ????????? don't touch
 */
  private Contract changePackageContract(ReceiveContractChangeCmd cmd, boolean optionChangeToo) {
    validateExistingProduct(cmd, cmd.getAfterPackageProductCode());
    validatePackageOptionRelation(
        cmd.getAfterPackageProductCode(),
        optionChangeToo ? cmd.getAfterOptionProductCode() : cmd.getBeforeOptionProductCode()
    );

    Contract pkgContract = contractRepository.findById(cmd.getPackageContractId()).orElseThrow(InvalidArgumentException::new);

    ProductFactory bfPkgProdFactory = productFactoryMap.get(cmd.getBeforePackageProductCode());
    ProductFactory afPkgProdFactory = productFactoryMap.get(cmd.getAfterPackageProductCode());

    List<String> termProdCodes = bfPkgProdFactory.getShouldBeTerminatedProductCodes(afPkgProdFactory);
    List<ProductSubscription> newBasicBenefitProdSubs
        = afPkgProdFactory.receiveSubscription(pkgContract, cmd.getChangeReceivedDateTime(), bfPkgProdFactory.getBasicBenefitProductCodes());

    messageStore.saveMessage(
        wrapEvent(pkgContract.changeContract(cmd.getOrderId(), cmd.getAfterPackageProductCode(), termProdCodes, newBasicBenefitProdSubs, cmd.getChangeReceivedDateTime()))
    );
    return contractRepository.save(pkgContract);
  }

  /*
. ?????? ??????(?????? ??????????????????=N)
  ex) all+?????? -> all+??????

  0) ???????????? ?????? ??? ?????? ???????????? ??????
  1) after??????(??????)??? ????????? ???????????? ??????
  2) before ?????? ?????? ????????????
  3) after ?????? ?????? ?????? ?????? (bill cycle??? package??? ??????)
  4) package ?????? ?????? (????????? ?????? ?????? ??????)

 3. ?????? ??????(?????? ??????????????????=Y)
  ex) all+?????? -> all+?????? & ??????

  0) ???????????? ?????? ??? ?????? ???????????? ??????
  1) after??????(??????)??? ????????? ???????????? ??????
  2) before ?????? ????????? ???????????? ??????
  3) after ?????? ?????? ?????? ?????? (bill cycle??? package??? ??????)
  4) package ?????? ?????? (????????? ?????? ?????? ??????)
 */
  private List<Contract> changeOptionContract(ReceiveContractChangeCmd cmd, boolean packageChangeToo) {
    validateExistingProduct(cmd, cmd.getAfterOptionProductCode());
    validatePackageOptionRelation(
        packageChangeToo ? cmd.getAfterPackageProductCode() : cmd.getBeforePackageProductCode(),
        cmd.getAfterOptionProductCode()
    );

    Contract pkgContract = contractRepository.findById(cmd.getPackageContractId()).orElseThrow(InvalidArgumentException::new);
    Contract bfOptContract = contractRepository.findById(cmd.getOptionContractId()).orElseThrow(InvalidArgumentException::new);
    
    List<ContractEventBase> events = new ArrayList<>();
    
    // ???????????? ??????
    Contract afOptContract = createContract(cmd.getCustomerId(), cmd.getOrderId(), cmd.getChangeReceivedDateTime(), cmd.getAfterOptionProductCode(), ContractType.OPTION);
    subscribeProducts(afOptContract, cmd.getChangeReceivedDateTime());
    afOptContract.setBillCycle(pkgContract.copyBillCycle());
    contractRepository.save(afOptContract);
    events.add(afOptContract.receiveSubscription());
    
    // ???????????? ??????
    if (cmd.isKeepingBeforeContract()) {
      events.add(bfOptContract.changeToUnit(cmd.getOrderId(), cmd.getChangeReceivedDateTime()));
    }
    else {
      events.add(bfOptContract.receiveTermination(cmd.getOrderId(), cmd.getChangeReceivedDateTime()));
    }
    packageService.changePackageComposition(pkgContract, bfOptContract, afOptContract, cmd.getChangeReceivedDateTime(), cmd.getOrderId());
  
    messageStore.saveMessage(
        events.stream().map(this::wrapEvent).collect(Collectors.toList())
    );
    return List.of(bfOptContract, afOptContract);
  }

  private void validateExistingProduct(ReceiveContractChangeCmd cmd, String afterProductCode) {
    Long sameProductExistedCount = contractRepository.countActiveContractByCustomerAndFeeProductCode(cmd.getCustomerId(), afterProductCode);
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
  public List<ContractDto> cancelContractChangeReceipt(CancelContractChangeCmd cmd) {
    List<Contract> contracts = contractRepository.findByLastOrderId(cmd.getOrderId());
    messageStore.saveMessage(
        contracts.stream()
            .map(c -> wrapEvent(c.cancelContractChange(cmd.getOrderId(), cmd.getCanceledChangeReceiptDateTime())))
            .collect(Collectors.toList())
    );
    return contracts.stream().map(ContractDto::of).collect(Collectors.toList());
  }
  
  @Override
  @Transactional
  public void markProductAuthorizedDateTime(long contractId, String productCode, LocalDateTime authorizedDateTime) {
    contractRepository.findWithProductsAndDiscountsById(contractId)
        .orElseThrow(IllegalArgumentException::new)
        .markProductAuthorizedDateTime(productCode, authorizedDateTime);
  }
  
  @Override
  @Transactional
  public ContractDto receiveCouponDiscount(ReceiveCouponDiscountDto dto) {
    DiscountPolicy discountPolicy = discountPolicyRepository.findByCouponPolicyCode(dto.getCouponPolicyCode()).orElseThrow(InvalidArgumentException::new);
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    messageStore.saveMessage(
        wrapEvent(contract.receiveCouponDiscount(discountPolicy, dto.getCouponId(), dto.getCouponUseReservedDateTime()))
    );
    return ContractDto.of(contract);
  }

  @Override
  @Transactional
  public ContractDto releaseCouponDiscount(ReleaseCouponDiscountDto dto) {
    DiscountPolicy discountPolicy = discountPolicyRepository.findByCouponPolicyCode(dto.getCouponPolicyCode()).orElseThrow(InvalidArgumentException::new);
    Contract contract = contractRepository.findById(dto.getContractId()).orElseThrow(InvalidArgumentException::new);
    messageStore.saveMessage(
        wrapEvent(contract.releaseCouponDiscount(discountPolicy, dto.getCouponId(), dto.getCouponUseReleasedDateTime()))
    );
    return ContractDto.of(contract);
  }

  @Override
  @Transactional
  public List<ContractDto> changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto dto) {
    List<Contract> contracts = contractRepository.findByCustomerId(dto.getCustomerId(), false);
    messageStore.saveMessage(
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
      return MessageEnvelope.wrapEvent(
          CONTRACT_AGGREGATE_TYPE,
          String.valueOf(e.getContractId()),
          CONTRACT_EVENT_BINDING,
          e.getClass().getSimpleName(),
          objectMapper.writeValueAsString(e)
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

  public List<ContractDto> findByContractId(long contractId, boolean withPackageOrOption, boolean includeProductAndDiscount) {
    return contractRepository.findByContractId(contractId, withPackageOrOption, includeProductAndDiscount)
        .stream()
        .map(ContractDto::of)
        .collect(Collectors.toList());
  }
  
  public List<CalculationResult> calculate(long contractId, boolean withPackageOrOption, LocalDate calculateDate) {
    var result = new ArrayList<CalculationResult>();
    List<Contract> contracts = contractRepository.findByContractId(contractId, withPackageOrOption, true);
    for (Contract c : contracts) {
      c.calculate(calculateDate).ifPresent(result::add);
    }
    return result;
  }
}
