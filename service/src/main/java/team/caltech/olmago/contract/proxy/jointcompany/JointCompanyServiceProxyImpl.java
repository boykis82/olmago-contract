package team.caltech.olmago.contract.proxy.jointcompany;

import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.proxy.jointcompany.dto.JointCompanyAuthenticationDto;

import java.time.LocalDateTime;
import java.util.List;

import static team.caltech.olmago.contract.proxy.jointcompany.dto.JointCompanyEnum.*;

public class JointCompanyServiceProxyImpl implements JointCompanyServiceProxy {
  // todo : 조회 전용으로 변경하자
  public Mono<JointCompanyAuthenticationDto> getAuthentications(long contractId) {
    return Mono.just(
        JointCompanyAuthenticationDto.builder()
            .contractId(contractId)
            .JointCompanyAuths(List.of(
                JointCompanyAuthenticationDto.JointCompanyAuthentication.builder().productCode("NM00000001").jointCompanyEnum(BAEMIN).firstAuthDtm(LocalDateTime.now()).lastAuthDtm(LocalDateTime.now()).build(),
                JointCompanyAuthenticationDto.JointCompanyAuthentication.builder().productCode("NM00000002").jointCompanyEnum(GOOGLE).firstAuthDtm(LocalDateTime.now()).lastAuthDtm(LocalDateTime.now()).build()
            ))
            .build()
    );
  }
}
