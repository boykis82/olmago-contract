package team.caltech.olmago.contract.customer;

import reactor.core.publisher.Mono;

public interface CustomerServiceProxy {
  Mono<LinkedMobilePhoneDto> findByCustomerId(long customerId);
}
