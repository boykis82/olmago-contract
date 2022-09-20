package team.caltech.olmago.contract.proxy.associatedcompany;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface AssociatedCompanyServiceProxy {
  // todo : 조회 전용으로 변경하자
  Mono<String> getAuthentications(long contractId);
}
