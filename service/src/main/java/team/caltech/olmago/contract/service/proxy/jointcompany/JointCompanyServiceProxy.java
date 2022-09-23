package team.caltech.olmago.contract.service.proxy.jointcompany;

import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.service.proxy.jointcompany.dto.JointCompanyAuthenticationDto;

public interface JointCompanyServiceProxy {
  // todo : 조회 전용으로 변경하자
  Mono<JointCompanyAuthenticationDto> getAuthentications(long contractId);
}
