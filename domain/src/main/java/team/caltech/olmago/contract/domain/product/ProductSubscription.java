package team.caltech.olmago.contract.domain.product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.domain.common.LifeCycle;
import team.caltech.olmago.contract.domain.contract.CalculationResult;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.discount.DiscountCalculationResult;
import team.caltech.olmago.contract.domain.discount.DiscountSubscription;
import team.caltech.olmago.contract.domain.exception.InvalidArgumentException;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "prod_sub")
public class ProductSubscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Version
  private int version;
  
  @ManyToOne
  @JoinColumn(name = "prod_cd")
  private Product product;
  
  @Embedded
  private LifeCycle lifeCycle;

  @ManyToOne
  @JoinColumn(name = "contract_id")
  private Contract contract;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "productSubscription")
  private List<DiscountSubscription> discountSubscriptions = new ArrayList<>();
  
  @Column(name = "last_auth_dtm")
  private LocalDateTime lastAuthorizedDateTime;
  
  @Builder
  public ProductSubscription(Contract contract,
                             Product product,
                             LocalDateTime subscriptionReceivedDateTime) {
    this.contract = contract;
    this.product = product;
    lifeCycle = new LifeCycle(subscriptionReceivedDateTime);
  }

  public ProductSubscription discountSubscriptions(List<DiscountSubscription> discountSubscriptions) {
    this.discountSubscriptions = discountSubscriptions;
    return this;
  }
  
  public void cancelSubscriptionReceipt(LocalDateTime cancelSubscriptionReceiptDateTime) {
    lifeCycle.cancelSubscriptionReceipt(cancelSubscriptionReceiptDateTime);
    discountSubscriptions.stream()
        .filter(ds -> ds.getLifeCycle().isSubscriptionReceived())
        .forEach(ds -> ds.cancelSubscriptionReceipt(cancelSubscriptionReceiptDateTime));
  }
  
  public void completeSubscription(LocalDateTime subscriptionCompletedDateTime) {
    lifeCycle.completeSubscription(subscriptionCompletedDateTime);
    discountSubscriptions.stream()
        .filter(ds -> ds.getLifeCycle().isSubscriptionReceived())
        .forEach(ds -> ds.completeSubscription(subscriptionCompletedDateTime));
  }
  
  public void receiveTermination(LocalDateTime terminationReceivedDateTime) {
    lifeCycle.receiveTermination(terminationReceivedDateTime);
    discountSubscriptions.stream()
        .filter(ds -> ds.getLifeCycle().isSubscriptionCompleted())
        .forEach(ds -> ds.receiveTermination(terminationReceivedDateTime));
  }
  
  public void cancelTerminationReceipt(LocalDateTime cancelTerminationReceiptDateTime) {
    lifeCycle.cancelTerminationReceipt(cancelTerminationReceiptDateTime);
    discountSubscriptions.stream()
        .filter(ds -> ds.getLifeCycle().isTerminationReceived())
        .forEach(ds -> ds.cancelTerminationReceipt(cancelTerminationReceiptDateTime));
  }
  
  public void completeTermination(LocalDateTime terminationCompletedDateTime) {
    lifeCycle.completeTermination(terminationCompletedDateTime);
    discountSubscriptions.stream()
        .filter(ds -> ds.getLifeCycle().isTerminationReceived())
        .forEach(ds -> ds.completeTermination(terminationCompletedDateTime));
  }

  public String getProductCode() {
    return product.getProductCode();
  }

  public void receiveCouponDiscount(DiscountPolicy discountPolicy, String couponId, LocalDateTime couponReservedDateTime) {
    // ???????????? ???????????? discountSubscriptions??? add
    discountSubscriptions.add(
        DiscountSubscription.builder()
            .discountPolicy(discountPolicy)
            .productSubscription(this)
            .couponId(couponId)
            .subRcvDtm(couponReservedDateTime)
            .build()
    );
  }
  
  public void releaseCouponDiscount(DiscountPolicy discountPolicy, String couponId, LocalDateTime couponUseReleasedDateTime) {
    // ???????????? ??? ???????????? ????????? ????????? ????????????
    discountSubscriptions.stream()
        .filter(ds -> ds.getDiscountPolicy().equals(discountPolicy) && ds.getLifeCycle().isSubscriptionReceived())
        .findAny()
        .orElseThrow(InvalidArgumentException::new)
        .cancelSubscriptionReceipt(couponUseReleasedDateTime);
  }
  
  public void changeMobilePhoneLinkedDiscount(List<DiscountPolicy> satisfiedMblPhoneLinkedDiscountPolicies, LocalDateTime changeDateTime) {
    terminateNotSatisfiedMobilePhoneLinkedDiscount(changeDateTime);
    subscribeSatisfiedMobilePhoneLinkedDiscount(satisfiedMblPhoneLinkedDiscountPolicies, changeDateTime);
  }

  private void terminateNotSatisfiedMobilePhoneLinkedDiscount(LocalDateTime changeDateTime) {
    discountSubscriptions.forEach(ds -> ds.terminateMobilePhoneLinkedDiscount(changeDateTime));
  }

  private void subscribeSatisfiedMobilePhoneLinkedDiscount(List<DiscountPolicy> satisfiedMblPhoneLinkedDiscountPolicies, LocalDateTime changeDateTime) {
    List<DiscountSubscription> newSubDcs =
        satisfiedMblPhoneLinkedDiscountPolicies.stream()
            .map(dp -> subscribeDiscount(dp, changeDateTime))
            .collect(Collectors.toList());
    newSubDcs.forEach(ds -> ds.completeSubscription(changeDateTime));
    discountSubscriptions.addAll(newSubDcs);
  }
  
  private DiscountSubscription subscribeDiscount(DiscountPolicy discountPolicy, LocalDateTime subRcvDtm) {
    return DiscountSubscription.builder()
        .discountPolicy(discountPolicy)
        .productSubscription(this)
        .subRcvDtm(subRcvDtm)
        .build();
  }
  
  public void authorize(LocalDateTime authorizedDateTime) {
    this.lastAuthorizedDateTime = authorizedDateTime;
  }
  
  public boolean isCalculationTarget() {
    return (lifeCycle.isSubscriptionReceived() || lifeCycle.isSubscriptionCompleted()) && product.getFeeVatIncluded() > 0;
  }
  
  public ProductCalculationResult calculate(LocalDate calculateDate) {
    return isCalculationTarget()
        ? new ProductCalculationResult(id, product.getProductCode(), product.getFeeVatIncluded(), calculateDiscounts(calculateDate))
        : null;
  }
  
  private List<DiscountCalculationResult> calculateDiscounts(LocalDate calculateDate) {
    /*
      ????????????????????? sort??????
      ???????????? = ????????????
      ?????? ???????????? ??????????????? ????????????
     */
    discountSubscriptions.sort(Comparator.comparingInt(p -> p.getDiscountPolicy().getDiscountPriority()));
    
    long balance = product.getFeeVatIncluded();
    List<DiscountCalculationResult> discountCalculationResults = new ArrayList<>();
    for (var ds : discountSubscriptions) {
      if (!ds.isDiscountTarget(calculateDate))
        continue;
      
      DiscountCalculationResult dcResult = ds.calculate(calculateDate, balance);
      if (dcResult.getDcAmountIncludeVat() != 0) {
        balance += dcResult.getDcAmountIncludeVat();
        discountCalculationResults.add(dcResult);
      }
      if (balance <= 0) {
        break;
      }
    }
    return discountCalculationResults;
  }
}
