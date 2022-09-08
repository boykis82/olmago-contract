package team.caltech.olmago.contract.product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.LifeCycle;
import team.caltech.olmago.contract.discount.DiscountSubscription;
import team.caltech.olmago.contract.plm.DiscountPolicy;
import team.caltech.olmago.contract.plm.Product;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
public class ProductSubscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Version
  private int version;
  
  @Column(nullable = false)
  @ManyToOne
  @JoinColumn(name = "productCode")
  private Product product;
  
  @Embedded
  private LifeCycle lifeCycle;
  
  @Column(nullable = false)
  @ManyToOne
  @JoinColumn(name = "contract_id")
  private Contract contract;
  
  private LocalDateTime associateCompanyAuthDtm;
  
  private String associateSystemId;
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
  private List<DiscountSubscription> discountSubscriptions = new ArrayList<>();
  
  @Builder
  public ProductSubscription(Contract contract,
                             Product product,
                             LocalDateTime subscriptionReceivedDateTime) {
    this.contract = contract;
    this.product = product;
    lifeCycle = new LifeCycle(subscriptionReceivedDateTime);
  }
  
  public ProductSubscription discountSubscriptions(DiscountSubscription ...discountSubscriptions) {
    this.discountSubscriptions = Arrays.asList(discountSubscriptions);
    return this;
  }
  
  public ProductSubscription discountSubscriptions(List<DiscountSubscription> discountSubscriptions) {
    this.discountSubscriptions = discountSubscriptions;
    return this;
  }
  
  public void completeSubscription(LocalDateTime subscriptionCompletedDateTime) {
    lifeCycle.completeSubscription(subscriptionCompletedDateTime);
    discountSubscriptions.forEach(ds -> ds.completeSubscription(subscriptionCompletedDateTime));
  }
  
  public void receiveTermination(LocalDateTime terminationReceivedDateTime) {
    lifeCycle.receiveTermination(terminationReceivedDateTime);
    discountSubscriptions.forEach(ds -> ds.receiveTermination(terminationReceivedDateTime));
  }
  
  public void cancelTerminationReceipt(LocalDateTime cancelTerminationReceiptDateTime) {
    lifeCycle.cancelTerminationReceipt(cancelTerminationReceiptDateTime);
    discountSubscriptions.forEach(ds -> ds.cancelTerminationReceipt(cancelTerminationReceiptDateTime));
  }
  
  public void completeTermination(LocalDateTime terminationCompletedDateTime) {
    lifeCycle.completeTermination(terminationCompletedDateTime);
    discountSubscriptions.forEach(ds -> ds.completeTermination(terminationCompletedDateTime));
  }

  public boolean isActive() {
    return lifeCycle.isActive();
  }
  
  public String getProductCode() {
    return product.getProductCode();
  }
  
  public void authenticate(String associateSystemId, LocalDateTime authenticatedDateTime) {
    this.associateCompanyAuthDtm = authenticatedDateTime;
    this.associateSystemId = associateSystemId;
  }
  
  public void receiveCouponDiscount(DiscountPolicy discountPolicy, String couponId, LocalDateTime couponReservedDateTime) {
    discountSubscriptions.add(
        DiscountSubscription.builder()
            .discountPolicy(discountPolicy)
            .productSubscription(this)
            .couponId(couponId)
            .subRcvDtm(couponReservedDateTime)
            .build()
    );
  }
}
