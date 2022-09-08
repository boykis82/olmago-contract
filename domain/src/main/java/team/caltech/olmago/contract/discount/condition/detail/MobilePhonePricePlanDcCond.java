package team.caltech.olmago.contract.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.discount.condition.DiscountCondition;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.customer.MobilePhonePricePlan;
import team.caltech.olmago.contract.customer.LinkedMobilePhoneInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MobilePhonePricePlanDcCond extends DiscountCondition {
  
  private final CustomerServiceProxy customerServiceProxy;
  private List<MobilePhonePricePlan> mobilePhonePricePlans = new ArrayList<>();
  
  public static MobilePhonePricePlanDcCond with(CustomerServiceProxy customerServiceProxy) {
    return new MobilePhonePricePlanDcCond(customerServiceProxy);
  }
  
  public MobilePhonePricePlanDcCond in(MobilePhonePricePlan ...mobilePhonePricePlans) {
    this.mobilePhonePricePlans = Arrays.asList(mobilePhonePricePlans);
    return this;
  }
  
  @Override
  public boolean satisfied(Contract contract) {
   LinkedMobilePhoneInfo linkedMobilePhoneInfo = customerServiceProxy.findByCustomerId(contract.getCustomerId());

    return mobilePhonePricePlans.stream()
        .anyMatch(mppp -> mppp == linkedMobilePhoneInfo.getMobilePhonePricePlan()
            &&
            linkedMobilePhoneInfo.getDcTargetUzooPassProductCode().equals(contract.getFeeProductCode())
        );
  }
}
