package team.caltech.olmago.contract.productauth.service;

import team.caltech.olmago.contract.productauth.dto.CompleteProductAuthDto;
import team.caltech.olmago.contract.productauth.dto.ExpireProductAuthDto;
import team.caltech.olmago.contract.productauth.dto.ProductAuthDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductAuthService {
  // 계약서비스로부터 받은 이벤트 구독시 호출하는 API
  void linkWithAssociatedCompanies(long contractId, List<String> productCodes, LocalDateTime linkDtm);
  
  // 제휴사에서 호출하는 API
  void completeAuth(CompleteProductAuthDto dto);
  void expireAuth(ExpireProductAuthDto dto);
  
  // 인증정보 조회
  List<ProductAuthDto> getProductAuths(long contractId);
}
