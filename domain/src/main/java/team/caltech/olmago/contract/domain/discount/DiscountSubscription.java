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
  1) ???????????? (2022-09-05)
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     
     ???????????????
     - dcstadt = 20220905
  
  2) ????????? ?????? ?????? (2022-09-20)
     - dcrgstdt = 20220920
     - dcstadt = 20220920
     
     ??????????????? (20221005)
     - dcstadt = 20221005
  
  3) ???????????? (2022-09-05) 1?????????
     ??????1
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     - dcenddt = 20221004
     - dcendrgstdt = null
     
     ???????????????
     ??????1
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     - dcenddt = 20221004
     - dcendrgstdt = null
     
     20221004->5???????????? ????????? ?????????????????? 3????????? ?????? ?????? & 1????????? ?????? ??????
     ??????1
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     - dcenddt = 20221004
     - dcendrgstdt = 20221005
     - term rcv dtm = 20221005~~
     
     ??????2
     - dcrgstdt = 20221005
     - dcstadt = 20221005
     - dcenddt = 20230104
     - dcendrgstdt = null
     - sub rcv dtm = 20221005~~
     
     5??? ?????? ?????? ?????????
     ??????1
     - dcrgstdt = 20220905
     - dcstadt = 20220905
     - dcenddt = 20221004
     - dcendrgstdt = 20221005
     - term cmpl dtm = 20221005~~
     
     ??????2
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
    // ?????????????????? ????????? ???????????? ?????? ??????
    if (discountStartDate.isAfter(calculateDate)) {
      return false;
    }
    if (discountPolicy.isDivideByUseDays()) {
      // ?????????????????? ????????? - 30??? ???????????? ?????? ??????
      if (discountEndDate.isBefore(calculateDate.minusDays(DiscountPolicy.DIVIDE_BY_USE_DAYS))) {
        return false;
      }
    } else {
      // ???????????? ?????? ???????????? ??? ???????????? ??????
      if (lifeCycle.isTerminationCompleted() || lifeCycle.isTerminationReceived()) {
        return false;
      }
      // ?????????????????? ????????? ???????????? ?????? ??????
      if (discountEndDate.isBefore(calculateDate)) {
        return false;
      }
    }
    return true;
  }
  
  private long getDiscountAmount(LocalDate calculateDate, long balance) {
    // ???????????? ?????????, ?????????????????? ???????????? ???????????? ????????? ??????. ??? ?????? ????????????
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
