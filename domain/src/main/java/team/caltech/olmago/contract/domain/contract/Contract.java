package team.caltech.olmago.contract.domain.contract;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.caltech.olmago.contract.domain.common.LifeCycle;
import team.caltech.olmago.contract.domain.contract.event.*;
import team.caltech.olmago.contract.domain.exception.InvalidArgumentException;
import team.caltech.olmago.contract.domain.plm.product.AvailableProductType;
import team.caltech.olmago.contract.domain.plm.product.BillPeriod;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.product.ProductSubscription;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity
@Table(
    name = "contract",
    indexes = {
        @Index(name = "contract_n1", columnList = "last_ord_id")
    }
)
public class Contract {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  protected void setId(long id) { this.id = id; }
  
  @Version
  private int version;
  
  @Column(name = "cust_id", nullable = false)
  private long customerId;
  
  @Column(name = "last_ord_id")
  private Long lastOrderId;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "cntrct_typ", length = 20, nullable = false)
  private ContractType contractType;

  @Embedded
  private LifeCycle lifeCycle;
  
  @Setter
  @Embedded
  private BillCycle billCycle;
  
  @Column(name = "last_pay_dtm")
  private LocalDateTime lastPaymentDtm;
  
  @Column(name = "bf_last_pay_dtm")
  private LocalDateTime beforeLastPaymentDtm;
  
  @Column(name = "unit_cnvt_dtm")
  private LocalDateTime unitContractConvertedDateTime;

  @Column(name = "fee_prod_cd", length = 10, nullable = false)
  private String feeProductCode;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contract")
  private final List<ProductSubscription> productSubscriptions = new ArrayList<>();
  
  @Builder
  public Contract(long customerId,
                  long orderId,
                  LocalDateTime subRcvDtm,
                  ContractType contractType,
                  String feeProductCode) {
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
    return new ContractSubscriptionReceived(id,
        lifeCycle.getSubscriptionReceivedDateTime(),
        lastOrderId,
        feeProductCode,
        this.productSubscriptions.stream()
            .map(ps -> new ContractSubscriptionReceived.Product(
                ps.getId(),
                ps.getProductCode(),
                ps.getDiscountSubscriptions().stream()
                    .map(ds -> new ContractSubscriptionReceived.Product.Discount(ds.getId(), ds.getDiscountPolicy().getDcPolicyCode()))
                    .collect(Collectors.toList())
                ))
            .collect(Collectors.toList())
    );
  }
  
  public ContractSubscriptionReceiptCanceled cancelSubscriptionReceipt(LocalDateTime cnclSubRcvDtm) {
    lifeCycle.cancelSubscriptionReceipt(cnclSubRcvDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionReceived())
        .forEach(ps -> ps.cancelSubscriptionReceipt(cnclSubRcvDtm));
    billCycle = null;
    lastPaymentDtm = null;
    return new ContractSubscriptionReceiptCanceled(id, cnclSubRcvDtm, lastOrderId);
  }
  
  public ContractSubscriptionCompleted completeSubscription(LocalDateTime subCmplDtm) {
    lifeCycle.completeSubscription(subCmplDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionReceived())
        .forEach(ps -> ps.completeSubscription(subCmplDtm));
    billCycle = BillCycle.of(subCmplDtm.toLocalDate(), BillPeriod.MONTHLY);
    lastPaymentDtm = subCmplDtm;
    return new ContractSubscriptionCompleted(id, subCmplDtm, lastOrderId, getAllProductCodes());
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
  
    return new ContractTerminationCompleted(id, termCmplDtm, lastOrderId);
  }
  
  public ProductsActivatedOrDeactivated activateOrDeactivateProducts(LocalDateTime regPayCmplDtm) {
    beforeLastPaymentDtm = lastPaymentDtm;
    lastPaymentDtm = regPayCmplDtm;
    billCycle = billCycle.next();
    
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isTerminationReceived())
        .forEach(ps -> ps.completeTermination(regPayCmplDtm));
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionReceived())
        .forEach(ps -> ps.completeSubscription(regPayCmplDtm));
  
    // 요금제코드 현행화
    setFeeProductCode();
    
    // 이벤트
    return new ProductsActivatedOrDeactivated(id, regPayCmplDtm, getSubProductCodes(), getTermProductCodes());
  }

  public ProductActivationHeld holdProductActivations(LocalDateTime regPayCnclDtm) {
    billCycle = billCycle.prev();
    lastPaymentDtm = beforeLastPaymentDtm;
    beforeLastPaymentDtm = null;
    
    return new ProductActivationHeld(id, regPayCnclDtm);
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
  
  private List<String> getTermProductCodes() {
    return getAllProductCodes(ps -> ps.getLifeCycle().isTerminationCompleted());
  }
  
  private List<String> getSubProductCodes() {
    return getAllProductCodes(ps -> ps.getLifeCycle().isSubscriptionCompleted());
  }
  
  private List<String> getAllProductCodes() {
    return getAllProductCodes(ps -> true);
  }
  
  private List<String> getAllProductCodes(Predicate<ProductSubscription> pred) {
    return productSubscriptions.stream()
        .filter(pred)
        .map(ProductSubscription::getProductCode)
        .collect(Collectors.toList());
  }
  
  public void markProductAuthrozedDateTime(String productCode, LocalDateTime authorizedDateTime) {
    productSubscriptions.stream()
        .filter(ps -> ps.getProductCode().equals(productCode))
        .findAny()
        .orElseThrow(IllegalStateException::new)
        .authorize(authorizedDateTime);
  }
}
