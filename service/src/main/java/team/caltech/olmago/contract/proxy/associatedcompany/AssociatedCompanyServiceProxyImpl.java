package team.caltech.olmago.contract.proxy.associatedcompany;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public class AssociatedCompanyServiceProxyImpl implements  AssociatedCompanyServiceProxy {
  public Mono<Void> subscribe(long contractId, List<String> productCodes, LocalDateTime subDtm) {
    // todo
    return Mono.just(1).then();
  }
  
  public Mono<Void> terminate(long contractId, List<String> productCodes, LocalDateTime termDtm) {
    // todo
    return Mono.just(1).then();
  }
}
