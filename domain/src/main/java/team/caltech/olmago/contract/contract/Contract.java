package team.caltech.olmago.contract.contract;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.caltech.olmago.contract.common.LifeCycle;
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
  public Contract(long customerId,
                  long lastOrderId,
                  ContractType contractType,
                  LocalDateTime subRcvDtm,
                  String feeProductCode) {
    this.customerId = customerId;
    this.lastOrderId = lastOrderId;
    this.contractType = contractType;
    this.lifeCycle = new LifeCycle(subRcvDtm);
    this.feeProductCode = feeProductCode;
  }
  
  public void addProductSubscriptions(List<ProductSubscription> productSubscriptions) {
    this.productSubscriptions.addAll(productSubscriptions);
  }
  
  public void cancelSubscriptionReceipt(LocalDateTime cnclSubRcvDtm) {
    lifeCycle.cancelSubscriptionReceipt(cnclSubRcvDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionReceived())
        .forEach(ps -> ps.cancelSubscriptionReceipt(cnclSubRcvDtm));
  }
  
  public void completeSubscription(LocalDateTime subCmplDtm) {
    lifeCycle.completeSubscription(subCmplDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionReceived())
        .forEach(ps -> ps.completeSubscription(subCmplDtm));
    billCycle = BillCycle.of(subCmplDtm.toLocalDate(), BillPeriod.MONTHLY);
  }
  
  public void receiveTermination(Long orderId, LocalDateTime termRcvDtm) {
    lifeCycle.receiveTermination(termRcvDtm);
    this.lastOrderId = orderId;
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isSubscriptionCompleted())
        .forEach(ps -> ps.receiveTermination(termRcvDtm));
  }
  
  public void cancelTerminationReceipt(Long orderId, LocalDateTime cnclTermRcvDtm) {
    lifeCycle.cancelTerminationReceipt(cnclTermRcvDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isTerminationReceived())
        .forEach(ps -> ps.cancelTerminationReceipt(cnclTermRcvDtm));
  }
  
  public void completeTermination(LocalDateTime termCmplDtm) {
    lifeCycle.completeTermination(termCmplDtm);
    productSubscriptions.stream()
        .filter(ps -> ps.getLifeCycle().isTerminationReceived())
        .forEach(ps -> ps.completeTermination(termCmplDtm));
  }
  
  public void completeRegularPayment(LocalDateTime regPayCmplDtm) {
    this.lastRegularPaymentCompletedDateTime = regPayCmplDtm;
    billCycle = billCycle.next();
    
    // 해지접수 -> 해지완료
    completeTermination(regPayCmplDtm);
    
    // 가입접수 -> 가입완료
    completeSubscription(regPayCmplDtm);
  
    // 요금제코드 현행화
    setFeeProductCode();
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
  
  public void changeContract(long orderId,
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
  }
  
  public void cancelContractChange(LocalDateTime cnclChangeDateTime) {
    if (contractType != ContractType.PACKAGE)
      throw new InvalidArgumentException();

    LocalDateTime changeDtm = getChangeDtm();
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

  public void setBillCycle(BillCycle billCycle) {
    this.billCycle = billCycle;
  }

  /*
    계약 변경 & 옵션 유지 시 단품으로 변경
   */
  public void changeToUnit(long orderId, LocalDateTime unitContractConvertedDateTime) {
    if (contractType != ContractType.OPTION) {
      throw new InvalidArgumentException();
    }
    this.lastOrderId = orderId;
    this.contractType = ContractType.UNIT;
    this.unitContractConvertedDateTime = unitContractConvertedDateTime;
  }
  
  /*
  계약 변경 & 옵션 유지 시 단품으로 변경했던거 취소하면 다시 옵션으로 원복
  */
  public void backToOption(long orderId, LocalDateTime unitContractConvertedDateTime) {
    if (contractType != ContractType.UNIT) {
      throw new InvalidArgumentException();
    }
    
    if (lastOrderId != orderId || !unitContractConvertedDateTime.equals(this.unitContractConvertedDateTime)) {
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
  
  public void receiveCouponDiscount(DiscountPolicy discountPolicy, String couponId, LocalDateTime couponReservedDateTime) {
    productSubscriptions.stream()
        .filter(ps -> ps.getProductCode().equals(feeProductCode))
        .findFirst()
        .orElseThrow(InvalidArgumentException::new)
        .receiveCouponDiscount(discountPolicy, couponId, couponReservedDateTime);
  }
}
