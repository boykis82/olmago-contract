package team.caltech.olmago.contract.discount;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.common.LifeCycle;
import team.caltech.olmago.contract.plm.DiscountPeriodType;
import team.caltech.olmago.contract.plm.DiscountPolicy;
import team.caltech.olmago.contract.plm.DiscountType;
import team.caltech.olmago.contract.product.ProductSubscription;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class DiscountSubscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Version
  private int version;
  
  @ManyToOne
  @JoinColumn(name = "dc_policy_id")
  private DiscountPolicy discountPolicy;
  
  @ManyToOne
  @JoinColumn(name = "prod_sub_id")
  private ProductSubscription productSubscription;
  
  @Embedded
  private LifeCycle lifeCycle;
  
  private LocalDate discountStartDate;
  private LocalDate discountEndDate;
  private LocalDate discountRegisterDate;
  private LocalDate discountEndRegisterDate;
  
  private String couponId;
  
  @Builder
  public DiscountSubscription(DiscountPolicy discountPolicy,
                              ProductSubscription productSubscription,
                              LocalDateTime subRcvDtm,
                              String couponId) {
    this.discountPolicy = discountPolicy;
    this.productSubscription = productSubscription;
    this.couponId = couponId;
    lifeCycle = new LifeCycle(subRcvDtm);
    
    discountRegisterDate = subRcvDtm.toLocalDate();
    calculateDiscountDate(subRcvDtm);
  }
  
  /*
  1) 신규가입 (2022-09-05)
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     
     결제완료시
     - dcstadt = 20220905
  
  2) 중간에 할인 교체 (2022-09-20)
     - dcrgstdt = 20220920
     - dcstadt = 20220920
     
     결제완료시 (20221005)
     - dcstadt = 20221005
  
  3) 신규가입 (2022-09-05) 1달짜리
     할인1
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     - dcenddt = 20221004
     - dcendrgstdt = null
     
     결제완료시
     할인1
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     - dcenddt = 20221004
     - dcendrgstdt = null
     
     20221004->5넘어가는 새벽에 할인터치하여 3달짜리 할인 부여 & 1달짜리 할인 끊기
     할인1
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     - dcenddt = 20221004
     - dcendrgstdt = 20221005
     - term rcv dtm = 20221005~~
     
     할인2
     - dcrgstdt = 20221005
     - dcstadt = 20221005
     - dcenddt = 20230104
     - dcendrgstdt = null
     - sub rcv dtm = 20221005~~
     
     5일 낮에 결제 성공시
     할인1
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     - dcenddt = 20221004
     - dcendrgstdt = 20221005
     - term cmpl dtm = 20221005~~
     
     할인2
     - dcrgstdt = 20221005
     - dcstadt = 20221005
     - dcenddt = 20230104
     - dcendrgstdt = null
     - sub cmpl dtm = 20221005~~
   */
  private void calculateDiscountDate(LocalDateTime dtm) {
    discountStartDate = dtm.toLocalDate();

    // 무제한 할인이면 99991231. 아니면 기간만큼 + month - 하룬
    if (discountPolicy.getDcPeriodType() == DiscountPeriodType.INFINITE) {
      discountEndDate = LocalDate.of(9999,12,31);
    } else {
      discountEndDate = discountStartDate.plusMonths(
          discountPolicy.getDcPeriodType().getMonths()
      ).minusDays(1);
    }
  
  }
  public void cancelSubscriptionReceipt(LocalDateTime cnclSubRcvDtm) {
    lifeCycle.cancelSubscriptionReceipt(cnclSubRcvDtm);
  }
  
  
  public void completeSubscription(LocalDateTime subCmplDtm) {
    lifeCycle.completeSubscription(subCmplDtm);
    calculateDiscountDate(subCmplDtm);
  }
  
  public void receiveTermination(LocalDateTime termRcvDtm) {
    lifeCycle.receiveTermination(termRcvDtm);
    discountEndRegisterDate = termRcvDtm.toLocalDate();
  }
  
  public void cancelTerminationReceipt(LocalDateTime cnclTermRcvDtm) {
    lifeCycle.cancelTerminationReceipt(cnclTermRcvDtm);
  }
  
  public void completeTermination(LocalDateTime termCmplDtm) {
    lifeCycle.completeTermination(termCmplDtm);
  }

  public void terminateMobilePhoneLinkedDiscount(LocalDateTime changeDateTime) {
    if (lifeCycle.isSubscriptionCompleted() &&
        discountPolicy.getDcType().equals(DiscountType.MOBILE_PHONE_PRICE_PLAN_LINKED)) {
      receiveTermination(changeDateTime);
      completeTermination(changeDateTime);
    }
  }
}
