package team.caltech.olmago.contract.proxy.associatedcompany;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public class AssociatedCompanyServiceProxyImpl implements  AssociatedCompanyServiceProxy {
  // todo : 조회 전용으로 변경하자
  public Mono<String> getAuthentications(long contractId) {
    return Mono.just("abc");
  }
}
