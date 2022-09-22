package team.caltech.olmago.contract.proxy.jointcompany.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JointCompanyEnum {
  BAEMIN("배달의 민족", "12.0.0.1", 8080),
  YANOLJA("야놀자", "12.0.0.2", 8080),
  GOOGLE("구글", "12.0.0.3", 8080),
  AMAZON("아마존", "12.0.0.4", 8080);
  
  private final String companyName;
  private final String ip;
  private final int port;
}
