package team.caltech.olmago.contract.plm;

import java.util.List;

import static team.caltech.olmago.contract.plm.AvailableProductType.*;
import static team.caltech.olmago.contract.plm.BillPeriod.MONTHLY;
import static team.caltech.olmago.contract.plm.DiscountPeriodType.*;
import static team.caltech.olmago.contract.plm.DiscountStartDateType.*;
import static team.caltech.olmago.contract.plm.DiscountType.*;
import static team.caltech.olmago.contract.plm.DiscountUnit.*;

public class PlmFixtures {
  public static List<DiscountPolicy> setupDiscountPolicies() {
    return
        List.of(
            DiscountPolicy.builder().dcPolicyCode("DCP0000001").dcPolicyName("우주패스All_Life_Standard_최초가입할인").dcUnit(AMOUNT).dcAmountOrRate(8900).dcType(THE_FIRST_SUBSCRIPTION).dcStartDateType(IMMEDIATELY).dcPeriodType(ONE_MONTH).build(),
            DiscountPolicy.builder().dcPolicyCode("DCP0000002").dcPolicyName("우주패스Mini_최초가입할인").dcUnit(AMOUNT).dcAmountOrRate(4800).dcType(THE_FIRST_SUBSCRIPTION).dcStartDateType(IMMEDIATELY).dcPeriodType(ONE_MONTH).build(),
            DiscountPolicy.builder().dcPolicyCode("DCP0000003").dcPolicyName("우주패스Slim_최초가입할인").dcUnit(AMOUNT).dcAmountOrRate(2800).dcType(THE_FIRST_SUBSCRIPTION).dcStartDateType(IMMEDIATELY).dcPeriodType(ONE_MONTH).build(),
    
            DiscountPolicy.builder().dcPolicyCode("DCB0000001").dcPolicyName("패키지 기본혜택 100%할인").dcUnit(RATE).dcAmountOrRate(100).dcType(BASIC_BENEFIT_FREE).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
    
            DiscountPolicy.builder().dcPolicyCode("DCO0000001").dcPolicyName("Flo 옵션 7900원 할인").dcUnit(AMOUNT).dcAmountOrRate(7900).dcType(DiscountType.OPTION).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
            DiscountPolicy.builder().dcPolicyCode("DCO0000002").dcPolicyName("게임패스 옵션 6000원 할인").dcUnit(AMOUNT).dcAmountOrRate(6000).dcType(DiscountType.OPTION).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
            
            DiscountPolicy.builder().dcPolicyCode("DCU0000001").dcPolicyName("게임패스 얼티밋 단품_최초가입할인").dcUnit(AMOUNT).dcAmountOrRate(11800).dcType(THE_FIRST_SUBSCRIPTION).dcStartDateType(IMMEDIATELY).dcPeriodType(ONE_MONTH).build(),
            DiscountPolicy.builder().dcPolicyCode("DCU0000002").dcPolicyName("Flo and Data 단품_최초가입할인").dcUnit(AMOUNT).dcAmountOrRate(7800).dcType(THE_FIRST_SUBSCRIPTION).dcStartDateType(IMMEDIATELY).dcPeriodType(ONE_MONTH).build(),
            DiscountPolicy.builder().dcPolicyCode("DCU0000003").dcPolicyName("Flo and Data 플러스 단품_최초가입할인").dcUnit(AMOUNT).dcAmountOrRate(8900).dcType(THE_FIRST_SUBSCRIPTION).dcStartDateType(IMMEDIATELY).dcPeriodType(ONE_MONTH).build(),
            DiscountPolicy.builder().dcPolicyCode("DCU0000004").dcPolicyName("Flo and Data 단품_이동전화요금제할인(프라임플러스,플래티넘,맥스)").dcUnit(AMOUNT).dcAmountOrRate(7900).dcType(MOBILE_PHONE_PRICE_PLAN_LINKED).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
            DiscountPolicy.builder().dcPolicyCode("DCU0000005").dcPolicyName("Flo and Data 단품_이동전화요금제할인(프라임,스페셜)").dcUnit(AMOUNT).dcAmountOrRate(5530).dcType(MOBILE_PHONE_PRICE_PLAN_LINKED).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
            DiscountPolicy.builder().dcPolicyCode("DCU0000006").dcPolicyName("Flo and Data 플러스 단품_이동전화요금제할인(플래티넘)").dcUnit(AMOUNT).dcAmountOrRate(9000).dcType(MOBILE_PHONE_PRICE_PLAN_LINKED).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
            DiscountPolicy.builder().dcPolicyCode("DCU0000007").dcPolicyName("Flo and Data 플러스 단품_이동전화요금제할인(프라임플러스,맥스)").dcUnit(AMOUNT).dcAmountOrRate(7900).dcType(MOBILE_PHONE_PRICE_PLAN_LINKED).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
            DiscountPolicy.builder().dcPolicyCode("DCU0000008").dcPolicyName("Flo and Data 플러스 단품_이동전화요금제할인(프라임,스페셜)").dcUnit(AMOUNT).dcAmountOrRate(5530).dcType(MOBILE_PHONE_PRICE_PLAN_LINKED).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
    
            DiscountPolicy.builder().dcPolicyCode("DCC0000001").dcPolicyName("쿠폰할인1").dcUnit(RATE).dcAmountOrRate(100).dcType(COUPON).dcStartDateType(IMMEDIATELY).dcPeriodType(ONE_MONTH).couponPolicyCode("CP00000001").build(),
    
            DiscountPolicy.builder().dcPolicyCode("DCM0000001").dcPolicyName("이동전화 요금제 연계(all/life) 100%할인(프라임플러스이상)").dcUnit(AMOUNT).dcAmountOrRate(9900).dcType(MOBILE_PHONE_PRICE_PLAN_LINKED).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
            DiscountPolicy.builder().dcPolicyCode("DCM0000002").dcPolicyName("이동전화 요금제 연계(all/life) 5000원 할인(프라임)").dcUnit(AMOUNT).dcAmountOrRate(5000).dcType(MOBILE_PHONE_PRICE_PLAN_LINKED).dcStartDateType(IMMEDIATELY).dcPeriodType(INFINITE).build(),
            
            DiscountPolicy.builder().dcPolicyCode("DCS0000001").dcPolicyName("이동전화 요금제 연계(all/life) 4900원 추가할인(프라임)_프로모션(20220901~20221231)").dcUnit(AMOUNT).dcAmountOrRate(4900).dcType(MOBILE_PHONE_PRICE_PLAN_LINKED).dcStartDateType(IMMEDIATELY).dcPeriodType(THREE_MONTHS).build()
        );
  }
  
/*
단품
  flo & data (7900)
    프라임 = 스페셜  5530 (70%)
    맥스 = 프라임플러스 7900
    플래티넘  7900
    
  flo & data plus (9000)
    프라임 = 스페셜   5530
    맥스 = 프라임플러스 7900
    플래티넘 9000

  wavve & data (9900)
    프라임     6930
    맥스      9900
    프라임플러스 9900
    플래      9900

  wavve & data plus (12300)
    프라임=스페셜 8610 (70%)
    맥스 9900
    프라임플러스  12300
    플래티넘 12300
    
  wavve & data premuium (15900)
    0플랜 ,스페셜    8610
    맥스      9900
    프라임   11130 (70%)
    프라임플러스  15900
    플래티넘  15900
    
패키지 (flo, wavve 연계) -> 없어졌나?
  프라임  5000
  플래티넘 7000
  
이동전화 요금제 연계 (all / ilfe) -> ok
  플래티넘
    100%
  
  프라임플러스
    100%
  
  프라임
    9/1 ~ 12/31 사이 가입
      3개월 무료 (5000+4900원으로 풀었나보다)
      그 뒤로 5000원
 */
  
  public static List<Product> setupProducts() {
    return List.of(
            Product.builder().productCode("NMP0000001").productName("우주패스All").availableProductType(PACKAGE).billPeriod(MONTHLY).feeVatIncluded(9900).isTheFirstSubscriptionDcTarget(true).build(),
            Product.builder().productCode("NMP0000002").productName("우주패스Life").availableProductType(PACKAGE).billPeriod(MONTHLY).feeVatIncluded(9900).isTheFirstSubscriptionDcTarget(true).build(),
            Product.builder().productCode("NMP0000003").productName("우주패스Mini").availableProductType(PACKAGE).billPeriod(MONTHLY).feeVatIncluded(4900).isTheFirstSubscriptionDcTarget(true).build(),
            Product.builder().productCode("NMP0000004").productName("우주패스Slim").availableProductType(PACKAGE).billPeriod(MONTHLY).feeVatIncluded(2900).isTheFirstSubscriptionDcTarget(true).build(),
    
            Product.builder().productCode("NMB0000001").productName("Google one(all)").availableProductType(BASIC_BENEFIT).billPeriod(MONTHLY).feeVatIncluded(2000).build(),
            Product.builder().productCode("NMB0000002").productName("아마존 무료배송").availableProductType(BASIC_BENEFIT).billPeriod(MONTHLY).feeVatIncluded(2000).build(),
            Product.builder().productCode("NMB0000003").productName("세븐일레븐").availableProductType(BASIC_BENEFIT).billPeriod(MONTHLY).feeVatIncluded(0).build(),
            Product.builder().productCode("NMB0000004").productName("투썸플레이스").availableProductType(BASIC_BENEFIT).billPeriod(MONTHLY).feeVatIncluded(0).build(),
    
            Product.builder().productCode("NMO0000001").productName("배달의민족").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(5000).build(),
            Product.builder().productCode("NMO0000002").productName("굽네치킨").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(5000).build(),
            Product.builder().productCode("NMO0000003").productName("wavve and data").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(9900).build(),
            Product.builder().productCode("NMO0000004").productName("flo and data").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(7900).build(),
            Product.builder().productCode("NMO0000005").productName("게임패스 얼티밋").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(11900).build(),
            Product.builder().productCode("NMO0000006").productName("Google one(mini)").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(1000).build(),
            Product.builder().productCode("NMO0000007").productName("wavve and data plus").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(12300).build(),
            Product.builder().productCode("NMO0000008").productName("flo and data plus").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(9000).build(),
            Product.builder().productCode("NMO0000009").productName("wavve and data premium").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).isTheFirstSubscriptionDcTarget(true).feeVatIncluded(15900).build(),
            Product.builder().productCode("NMO0000010").productName("야놀자").availableProductType(AvailableProductType.OPTION).billPeriod(MONTHLY).feeVatIncluded(5000).isTheFirstSubscriptionDcTarget(false).build()
    );
  }
}
