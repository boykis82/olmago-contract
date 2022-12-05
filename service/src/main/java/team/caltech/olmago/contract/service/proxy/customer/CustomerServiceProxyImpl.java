package team.caltech.olmago.contract.service.proxy.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.customer.CustomerDto;

public class CustomerServiceProxyImpl implements CustomerServiceProxy {
  private final WebClient webClient;
  
  @Autowired
  public CustomerServiceProxyImpl(WebClient webClient) {
    this.webClient = webClient;
  }
  
  @Override
  public Mono<CustomerDto> findByCustomerId(long customerId) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/customers/{customer-id}").build(customerId))
        .retrieve()
        .bodyToMono(CustomerDto.class);
  }
}
