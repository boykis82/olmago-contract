package team.caltech.olmago.contract.customer;

import reactor.core.publisher.Mono;

// Customer 서비스와 통신하기 위한 proxy
public interface CustomerServiceProxy {
  Mono<LinkedMobilePhoneDto> findByCustomerId(long customerId);
}
