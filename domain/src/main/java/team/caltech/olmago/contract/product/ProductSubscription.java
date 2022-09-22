package team.caltech.olmago.contract.product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.common.LifeCycle;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.discount.DiscountSubscription;
import team.caltech.olmago.contract.exception.InvalidArgumentException;
import team.caltech.olmago.contract.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.plm.product.Product;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Entity
@Getter
public class ProductSubscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Version
  private int version;
  
  @ManyToOne
  @JoinColumn(name = "productCode")
  private Product product;
  
  @Embedded
  private LifeCycle lifeCycle;

  @ManyToOne
  @JoinColumn(name = "contract_id")
  private Contract contract;
  
  // 제휴사 인증 관련된 건 별도 서비스로 빼자.
  //private LocalDateTime associateCompanyAuthDtm;
  //private String associateSystemId;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "productSubscription")
  private List<DiscountSubscription> discountSubscriptions = new ArrayList<>();
  
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
    // 쿠폰할인 생성해서 discountSubscriptions에 add
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
    // 쿠폰할인 중 가입예약 중인거 찾아서 가입취소
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
  
}
