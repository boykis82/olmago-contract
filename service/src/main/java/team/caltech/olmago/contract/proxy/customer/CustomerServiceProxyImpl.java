package team.caltech.olmago.contract.proxy.customer;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.customer.LinkedMobilePhoneInfo;
import team.caltech.olmago.contract.customer.MobilePhonePricePlan;

@Component
public class CustomerServiceProxyImpl implements CustomerServiceProxy {
  @Override
  public Mono<LinkedMobilePhoneInfo> findByCustomerId(long customerId) {
    // todo - web연동으로 변경
    return Mono.just(LinkedMobilePhoneInfo.builder()
        .mobilePhonePricePlan(MobilePhonePricePlan.PLATINUM)
        .customerId(customerId)
        .mobilePhoneNumber("01012345678")
        .mobilePhoneSvcMgmtNum(7102112312L)
        .build()
    );
  }
}
