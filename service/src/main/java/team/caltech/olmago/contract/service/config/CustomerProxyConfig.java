package team.caltech.olmago.contract.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.service.proxy.customer.CustomerServiceProxyImpl;

@Configuration
public class CustomerProxyConfig {
  @Bean
  public WebClient customerWebClient(WebClient.Builder webClientBuilder,
                                     @Value("${app.customer-service.host}") String host,
                                     @Value("${app.customer-service.port}") int port) {
    return webClientBuilder.baseUrl("http://" + host + ":" + port + "/swing/api/v1").build();
  }
  
  @Bean
  public CustomerServiceProxy swingProxy(WebClient customerWebClient) {
    return new CustomerServiceProxyImpl(customerWebClient);
  }
}
