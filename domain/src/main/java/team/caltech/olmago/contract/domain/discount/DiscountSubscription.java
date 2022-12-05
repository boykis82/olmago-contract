package team.caltech.olmago.contract.domain.discount;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.domain.common.LifeCycle;
import team.caltech.olmago.contract.domain.plm.discount.DiscountType;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPeriodType;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.plm.discount.DiscountUnit;
import team.caltech.olmago.contract.domain.product.ProductCalculationResult;
import team.caltech.olmago.contract.domain.product.ProductSubscription;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "dc_sub")
public class DiscountSubscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Version
  private int version;
  
  @ManyToOne
  @JoinColumn(name = "dc_plcy_id")
  private DiscountPolicy discountPolicy;
  
  @ManyToOne
  @JoinColumn(name = "prod_sub_id")
  private ProductSubscription productSubscription;
  
  @Embedded
  private LifeCycle lifeCycle;
  
  @Column(name = "dc_sta_dt", nullable = false)
  private LocalDate discountStartDate;
  @Column(name = "dc_end_dt", nullable = false)
  private LocalDate discountEndDate;
  @Column(name = "dc_rgst_dt", nullable = false)
  private LocalDate discountRegisterDate;
  @Column(name = "dc_end_rgst_dt")
  private LocalDate discountEndRegisterDate;
  @Column(name = "copn_id", length = 100)
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
    discountEndDate = discountStartDate.plusMonths(discountPolicy.getDcPeriodType().getMonths()).minusDays(1);
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
    if (lifeCycle.isSubscriptionCompleted() && discountPolicy.getDcType() == DiscountType.MOBILE_PHONE_PRICE_PLAN_LINKED) {
      receiveTermination(changeDateTime);
      completeTermination(changeDateTime);
    }
  }
  
  public DiscountCalculationResult calculate(LocalDate calculateDate, long balance) {
    return isDiscountTarget(calculateDate)
        ? new DiscountCalculationResult(id, discountPolicy.getDcPolicyCode(), getDiscountAmount(calculateDate, balance) * -1)
        : null;
  }
  
  public boolean isDiscountTarget(LocalDate calculateDate) {
    // 할인시작일이 기준일 이후이면 대상 아님
    if (discountStartDate.isAfter(calculateDate)) {
      return false;
    }
    if (discountPolicy.isDivideByUseDays()) {
      // 할인종료일이 기준일 - 30일 이전이면 대상 아님
      if (discountEndDate.isBefore(calculateDate.minusDays(DiscountPolicy.DIVIDE_BY_USE_DAYS))) {
        return false;
      }
    } else {
      // 해지접수 또는 해지완료 시 할인대상 아님
      if (lifeCycle.isTerminationCompleted() || lifeCycle.isTerminationReceived()) {
        return false;
      }
      // 할인종료일이 기준일 이전이면 대상 아님
      if (discountEndDate.isBefore(calculateDate)) {
        return false;
      }
    }
    return true;
  }
  
  private long getDiscountAmount(LocalDate calculateDate, long balance) {
    // 만월금액 구하고, 일할계산대상 아니거나 첫결제면 그대로 리턴. 그 외는 일할계산
    long dcAmount = calculateDiscountAmountOfFullMonthUse(balance);
    if (!discountPolicy.isDivideByUseDays() || isFirstCalculation(calculateDate)) {
      return dcAmount;
    }
    return calculateDiscountAmountOfUseDays(dcAmount, calculateDate);
  }
  
  private long calculateDiscountAmountOfFullMonthUse(long balance) {
    return discountPolicy.getDcUnit() == DiscountUnit.AMOUNT
        ? Math.min(balance, discountPolicy.getDcAmountOrRate())
        : Math.round(balance * discountPolicy.getDcAmountOrRate() / 100.0);
  }
  
  private boolean isFirstCalculation(LocalDate calculateDate) {
    return productSubscription.getLifeCycle().getSubscriptionReceivedDateTime().toLocalDate().equals(calculateDate);
  }
  
  private long calculateDiscountAmountOfUseDays(long dcAmountFullMonthUse, LocalDate calculateDate) {
    LocalDate realDcStartDate = calculateRealDcStartDate(calculateDate);
    LocalDate realDcEndDate = calculateRealDcEndDate(calculateDate);
    long useDays = Period.between(realDcStartDate, realDcEndDate).getDays();
    double dcAmountOfUseDays = dcAmountFullMonthUse * useDays * 1.0 / DiscountPolicy.DIVIDE_BY_USE_DAYS;
    return Math.round(dcAmountOfUseDays);
  }

  private LocalDate calculateRealDcStartDate(LocalDate calculateDate) {
    LocalDate before30Days = calculateDate.minusDays(DiscountPolicy.DIVIDE_BY_USE_DAYS);
    return before30Days.isBefore(discountStartDate) ? discountStartDate : before30Days;
  }
  
  private LocalDate calculateRealDcEndDate(LocalDate calculateDate) {
    LocalDate discountEndStrdDate = discountEndDate;
    if (discountEndRegisterDate != null && discountEndRegisterDate.isBefore(discountEndDate)) {
      discountEndStrdDate = discountEndRegisterDate;
    }
    return calculateDate.isBefore(discountEndStrdDate) ? calculateDate : discountEndStrdDate;
  }

}
