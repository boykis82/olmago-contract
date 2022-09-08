package team.caltech.olmago.contract.proxy.customer;

import org.springframework.stereotype.Component;
import team.caltech.olmago.contract.proxy.customer.dto.LinkedMobilePhoneInfo;

@Component
public class CustomerServiceProxyImpl implements CustomerServiceProxy {
  @Override
  public LinkedMobilePhoneInfo findByCustomerId(long customerId) {
    // todo - web연동으로 변경
    return LinkedMobilePhoneInfo.builder()
        .mobilePhonePricePlan(MobilePhonePricePlan.PLATINUM)
        .customerId(customerId)
        .mobilePhoneNumber("01012345678")
        .mobilePhoneSvcMgmtNum(7102112312L)
        .build();
  }
}
