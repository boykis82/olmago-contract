package team.caltech.olmago.contract.contract;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.caltech.olmago.contract.common.LifeCycle;
import team.caltech.olmago.contract.contract.event.*;
import team.caltech.olmago.contract.exception.InvalidArgumentException;
import team.caltech.olmago.contract.plm.AvailableProductType;
import team.caltech.olmago.contract.plm.BillPeriod;
import team.caltech.olmago.contract.plm.DiscountPolicy;
import team.caltech.olmago.contract.plm.Product;
import team.caltech.olmago.contract.product.ProductSubscription;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "contract")
public class Contract {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Version
  private int version;
  
  @Column(nullable = false)
  private long customerId;
  
  @Column(nullable = false)
  private Long lastOrderId;
  
  @Enumerated(EnumType.STRING)
  private ContractType contractType;

  @Embedded
  private LifeCycle lifeCycle;
  
  @Setter
  @Embedded
  private BillCycle billCycle;
  
  private LocalDateTime lastRegularPaymentCompletedDateTime;

  private LocalDateTime unitContractConvertedDateTime;

  @Column(nullable = false)
  private String feeProductCode;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contract")
  private final List<ProductSubscription> productSubscriptions = new ArrayList<>();
  
  @Setter
  private Long packageId;
  
  @Builder
  public Contract(long id,
                  long customerId,
                  long orderId,
                  LocalDateTime subRcvDtm,
                  ContractType contractType,
                  String feeProductCode) {
    this.id = id;
    this.customerId = customerId;
    this.lastOrderId = orderId;
    this.lifeCycle = new LifeCycle(subRcvDtm);
    this.contractType = contractType;
    this.feeProductCode = feeProductCode;
  }
  
  public void addProductSubscriptions(List<ProductSubscription> productSubscriptions) {
    this.productSubscriptions.addAll(productSubscriptions);
  }
  
  public ContractSubscriptionReceived receiveSubscription() {
    return new ContractSubscriptionReceived(id, lifeCycle.getSubscriptionReceivedDateTime(), lastOrderId, feeProductCode);
  }
  
  public ContractSubscriptionReceiptCanceled cancelSubscriptionReceipt(LocalDateTime cnclSubRcvDtm) {
    lifeCycle.cancelSubscriptionReceipt(cnclSubRcvDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionReceived())
        .forEach(ps -> ps.cancelSubscriptionReceipt(cnclSubRcvDtm));
    return new ContractSubscriptionReceiptCanceled(id, cnclSubRcvDtm, lastOrderId);
  }
  
  public ContractSubscriptionCompleted completeSubscription(LocalDateTime subCmplDtm) {
    lifeCycle.completeSubscription(subCmplDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionReceived())
        .forEach(ps -> ps.completeSubscription(subCmplDtm));
    billCycle = BillCycle.of(subCmplDtm.toLocalDate(), BillPeriod.MONTHLY);
  
    return new ContractSubscriptionCompleted(id, subCmplDtm);
  }
  
  public ContractTerminationReceived receiveTermination(Long orderId, LocalDateTime termRcvDtm) {
    lifeCycle.receiveTermination(termRcvDtm);
    this.lastOrderId = orderId;
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionCompleted())
        .forEach(ps -> ps.receiveTermination(termRcvDtm));
    
    return new ContractTerminationReceived(id, termRcvDtm, orderId);
  }
  
  public ContractTerminationReceiptCanceled cancelTerminationReceipt(Long orderId, LocalDateTime cnclTermRcvDtm) {
    lifeCycle.cancelTerminationReceipt(cnclTermRcvDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isTerminationReceived())
        .forEach(ps -> ps.cancelTerminationReceipt(cnclTermRcvDtm));
  
    return new ContractTerminationReceiptCanceled(id, cnclTermRcvDtm, orderId);
  }
  
  public ContractTerminationCompleted completeTermination(LocalDateTime termCmplDtm) {
    lifeCycle.completeTermination(termCmplDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isTerminationReceived())
        .forEach(ps -> ps.completeTermination(termCmplDtm));
  
    return new ContractTerminationCompleted(id, termCmplDtm);
  }
  
  public RegularPaymentCompleted completeRegularPayment(LocalDateTime regPayCmplDtm) {
    this.lastRegularPaymentCompletedDateTime = regPayCmplDtm;
    billCycle = billCycle.next();
  
    // 코드 중복 제거를 위해 stream 미리 빼놓기
    Stream<ProductSubscription> subProductsStream = productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionReceived());
    Stream<ProductSubscription> termProductsStream = productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isTerminationReceived());
    
    // event
    RegularPaymentCompleted event = new RegularPaymentCompleted(
        id, regPayCmplDtm,
        subProductsStream.map(ProductSubscription::getProductCode).collect(Collectors.toList()),
        termProductsStream.map(ProductSubscription::getProductCode).collect(Collectors.toList())
    );
    
    // 해지접수 -> 해지완료
    termProductsStream.forEach(ps -> ps.completeTermination(regPayCmplDtm));
    
    // 가입접수 -> 가입완료
    subProductsStream.forEach(ps -> ps.completeSubscription(regPayCmplDtm));
  
    // 요금제코드 현행화
    setFeeProductCode();
    return event;
  }
  
  private void setFeeProductCode() {
    this.feeProductCode = productSubscriptions.stream()
        .filter(ps -> ps.getProduct().getAvailableProductType() != AvailableProductType.BASIC_BENEFIT
            &&
            ps.getLifeCycle().isSubscriptionCompleted()
        )
        .map(ProductSubscription::getProductCode)
        .findAny()
        .orElseThrow(IllegalStateException::new);
  }
  
  public ContractChanged changeContract(long orderId,
                             String feeProductCode,
                             List<String> termProdCodes,
                             List<ProductSubscription> newBasicBenefitProductSubscriptions,
                             LocalDateTime changeDateTime) {
    if (contractType != ContractType.PACKAGE)
      throw new InvalidArgumentException();

    receiveTerminationOfUnavailableBasicBenefitProduct(termProdCodes, changeDateTime);
    addProductSubscriptions(newBasicBenefitProductSubscriptions);
    
    this.feeProductCode = feeProductCode;
    this.lastOrderId = orderId;
    
    return new ContractChanged(id, changeDateTime, orderId);
  }
  
  public ContractChangeCanceled cancelContractChange(long orderId, LocalDateTime cnclChangeDateTime) {
    LocalDateTime changeDtm = getChangeDtm();
    // pkg 상품 변경 취소 또는 option 상품 변경 취소
    if (contractType == ContractType.PACKAGE ||
        contractType == ContractType.OPTION) {
      // 가입예약 취소
      productSubscriptions.stream()
          .filter(ps -> ps.getLifeCycle().getSubscriptionReceivedDateTime().equals(changeDtm))
          .forEach(ps -> ps.cancelSubscriptionReceipt(cnclChangeDateTime));
      // 해지예약 취소
      productSubscriptions.stream()
          .filter(ps -> ps.getLifeCycle().getTerminationReceivedDateTime().equals(changeDtm))
          .forEach(ps -> ps.cancelTerminationReceipt(cnclChangeDateTime));
      setFeeProductCode();
    }
    // option이었다가 unit으로 변경한 경우 (기존 계약 활용)
    else if (contractType == ContractType.UNIT) {
      backToOption(changeDtm);
    }
  
    return new ContractChangeCanceled(id, cnclChangeDateTime, lastOrderId);
  }
  
  private LocalDateTime getChangeDtm() {
    return productSubscriptions.stream()
        .filter(ps -> ps.getProduct().getAvailableProductType() == AvailableProductType.PACKAGE && ps.getLifeCycle().isSubscriptionReceived())
        .findFirst()
        .orElseThrow(IllegalStateException::new)
        .getLifeCycle().getSubscriptionReceivedDateTime();
  }

  private void receiveTerminationOfUnavailableBasicBenefitProduct(List<String> shouldBeTerminatedProductCodes,
                                                                  LocalDateTime chgDtm) {
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionCompleted()
            &&
            shouldBeTerminatedProductCodes.stream()
                .anyMatch(pc -> pc.equals(ps.getProductCode()))
        )
        .forEach(ps -> ps.receiveTermination(chgDtm));
  }

  public BillCycle copyBillCycle() {
    return BillCycle.builder()
        .theFirstBillStartDate(billCycle.getTheFirstBillStartDate())
        .monthsPassed(billCycle.getMonthsPassed())
        .build();
  }

  /*
    계약 변경 & 옵션 유지 시 단품으로 변경
   */
  public ContractChanged changeToUnit(long orderId, LocalDateTime unitContractConvertedDateTime) {
    if (contractType != ContractType.OPTION) {
      throw new InvalidArgumentException();
    }
    this.lastOrderId = orderId;
    this.contractType = ContractType.UNIT;
    this.unitContractConvertedDateTime = unitContractConvertedDateTime;
  
    return new ContractChanged(id, unitContractConvertedDateTime, orderId);
  }
  
  /*
  계약 변경 & 옵션 유지 시 단품으로 변경했던거 취소하면 다시 옵션으로 원복
  */
  private void backToOption(LocalDateTime unitContractConvertedDateTime) {
    if (contractType != ContractType.UNIT) {
      throw new InvalidArgumentException();
    }
    if (!unitContractConvertedDateTime.equals(this.unitContractConvertedDateTime)) {
      throw new IllegalStateException();
    }
    this.contractType = ContractType.OPTION;
    this.unitContractConvertedDateTime = null;
  }
  
  public void validateAvailableProductType(Product product) {
    if (contractType == ContractType.PACKAGE) {
      if (product.getAvailableProductType() != AvailableProductType.PACKAGE &&
          product.getAvailableProductType() != AvailableProductType.BASIC_BENEFIT) {
        throw new InvalidArgumentException();
      }
    }
    else {
      if (product.getAvailableProductType() != AvailableProductType.OPTION &&
          product.getAvailableProductType() != AvailableProductType.UNIT_AND_OPTION) {
        throw new InvalidArgumentException();
      }
    }
  }
  
  public DiscountChanged receiveCouponDiscount(DiscountPolicy discountPolicy, String couponId, LocalDateTime couponReservedDateTime) {
    getFeeProductSubscription().receiveCouponDiscount(discountPolicy, couponId, couponReservedDateTime);
    return new DiscountChanged(id, couponReservedDateTime);
  }
  
  public ContractEventBase releaseCouponDiscount(DiscountPolicy discountPolicy, String couponId, LocalDateTime couponUseReleasedDateTime) {
    getFeeProductSubscription().releaseCouponDiscount(discountPolicy, couponId, couponUseReleasedDateTime);
    return new DiscountChanged(id, couponUseReleasedDateTime);
  }

  public DiscountChanged changeMobilePhoneLinkedDiscount(List<DiscountPolicy> satisfiedMblPhoneLinkedDiscountPolicies, LocalDateTime changeDateTime) {
    getFeeProductSubscription().changeMobilePhoneLinkedDiscount(satisfiedMblPhoneLinkedDiscountPolicies, changeDateTime);
    return new DiscountChanged(id, changeDateTime);
  }

  private ProductSubscription getFeeProductSubscription() {
    return productSubscriptions.stream()
        .filter(ps -> ps.getProductCode().equals(feeProductCode))
        .findFirst()
        .orElseThrow(InvalidArgumentException::new);
  }

}
