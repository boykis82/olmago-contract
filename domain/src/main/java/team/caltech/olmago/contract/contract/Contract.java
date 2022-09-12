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
  }

  public void changeContract(String feeProductCode,
                             List<String> termProdCodes,
                             List<ProductSubscription> newBasicBenefitProductSubscriptions,
                             LocalDateTime changeDateTime) {
    if (contractType != ContractType.PACKAGE)
      throw new InvalidArgumentException();

    receiveTerminationOfUnavailableBasicBenefitProduct(termProdCodes, changeDateTime);
    addProductSubscriptions(newBasicBenefitProductSubscriptions);
    this.feeProductCode = feeProductCode;
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

  public void changeToUnit(long orderId, LocalDateTime unitContractConvertedDateTime) {
    if (contractType != ContractType.OPTION) {
      throw new InvalidArgumentException();
    }
    this.lastOrderId = orderId;
    this.contractType = ContractType.UNIT;
    this.unitContractConvertedDateTime = unitContractConvertedDateTime;
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
