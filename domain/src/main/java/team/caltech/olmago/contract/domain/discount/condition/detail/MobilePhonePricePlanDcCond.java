package team.caltech.olmago.contract.domain.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.discount.condition.DiscountCondition;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.customer.MobilePhonePricePlan;
import team.caltech.olmago.contract.domain.customer.LinkedMobilePhoneDto;

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
    Optional<LinkedMobilePhoneDto> linkedMobilePhoneInfo =
        customerServiceProxy.findByCustomerId(contract.getCustomerId()).blockOptional();
    
    return linkedMobilePhoneInfo.filter(
        mobilePhoneInfo -> mobilePhonePricePlans.stream()
            .anyMatch(mppp -> mobilePhoneInfo.matchMobilePhonePricePlanAndUzooPassProductCode(mppp, contract.getFeeProductCode()))
    ).isPresent();
  }
}
