package team.caltech.olmago.contract.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.discount.condition.DiscountCondition;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.customer.MobilePhonePricePlan;
import team.caltech.olmago.contract.customer.LinkedMobilePhoneInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    Optional<LinkedMobilePhoneInfo> linkedMobilePhoneInfo =
        customerServiceProxy.findByCustomerId(contract.getCustomerId()).blockOptional();
    
    return linkedMobilePhoneInfo.filter(
        mobilePhoneInfo -> mobilePhonePricePlans.stream()
            .anyMatch(mppp -> mobilePhoneInfo.matchMobilePhonePricePlanAndUzooPassProductCode(mppp, contract.getFeeProductCode()))
    ).isPresent();
  }
}
