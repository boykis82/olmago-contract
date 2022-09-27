package team.caltech.olmago.contract.productauth.proxy.contract;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Component
public class ContractServiceProxyImpl {
  private final WebClient contractWebClient;
  
  public Mono<ContractDto> getContract(long contractId) {
    return contractWebClient.get()
        .uri(ub -> ub.path("/{id}")
            .queryParam("withPackageOrOption", false)
            .queryParam("includeProductSubscription", true)
            .queryParam("includeDiscountSubscription", false)
            .build(contractId)
        )
        .retrieve()
        .bodyToMono(ContractDto.class);
  }
}
