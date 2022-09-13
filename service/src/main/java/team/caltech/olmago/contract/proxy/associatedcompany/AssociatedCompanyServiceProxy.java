package team.caltech.olmago.contract.proxy.associatedcompany;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface AssociatedCompanyServiceProxy {
  Mono<Void> subscribe(long contractId, List<String> productCodes, LocalDateTime subDtm);
  Mono<Void> terminate(long contractId, List<String> productCodes, LocalDateTime termDtm);
}
