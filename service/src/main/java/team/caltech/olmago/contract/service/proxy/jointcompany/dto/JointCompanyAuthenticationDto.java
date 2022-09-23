package team.caltech.olmago.contract.service.proxy.jointcompany.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class JointCompanyAuthenticationDto {
  @RequiredArgsConstructor
  @Getter
  @Builder
  public static class JointCompanyAuthentication {
    private final String productCode;
    private final JointCompanyEnum jointCompanyEnum;
    private final LocalDateTime firstAuthDtm;
    private final LocalDateTime lastAuthDtm;
  }
  
  private final long contractId;
  private final List<JointCompanyAuthentication> JointCompanyAuths;
}
