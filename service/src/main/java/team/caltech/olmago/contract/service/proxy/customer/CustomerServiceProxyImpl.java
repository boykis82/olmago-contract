package team.caltech.olmago.contract.service.proxy.customer;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.customer.LinkedMobilePhoneDto;
import team.caltech.olmago.contract.domain.customer.MobilePhonePricePlan;

@Component
public class CustomerServiceProxyImpl implements CustomerServiceProxy {
  @Override
  public Mono<LinkedMobilePhoneDto> findByCustomerId(long customerId) {
    // todo - web연동으로 변경
    return Mono.just(LinkedMobilePhoneDto.builder()
        .mobilePhonePricePlan(MobilePhonePricePlan.PLATINUM)
        .customerId(customerId)
        .mobilePhoneNumber("01012345678")
        .mobilePhoneSvcMgmtNum(7102112312L)
        .build()
    );
  }
}
