package team.caltech.olmago.contract.productauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Scheduler;
import team.caltech.olmago.contract.productauth.domain.ProductAuth;
import team.caltech.olmago.contract.productauth.domain.ProductAuthId;
import team.caltech.olmago.contract.productauth.domain.ProductAuthRepository;
import team.caltech.olmago.contract.productauth.dto.CompleteProductAuthDto;
import team.caltech.olmago.contract.productauth.dto.ExpireProductAuthDto;
import team.caltech.olmago.contract.productauth.dto.ProductAuthDto;
import team.caltech.olmago.contract.productauth.proxy.contract.ContractDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductAuthServiceImpl implements ProductAuthService {
  private final ProductAuthRepository productAuthRepository;
  
  //private final EAIClient eaiClient;
  private final Scheduler otherServiceCommScheduler;
  
  @Override
  @Transactional
  public void linkWithAssociatedCompanies(ContractDto contractDto, LocalDateTime linkDtm) {
    // 살아있는 것만 인증요청보낸다.
    contractDto.getProductSubscriptions().stream()
        .filter(ContractDto.ProductSubscriptionDto::isActive)
        .forEach(ps -> linkWithAssociatedCompany(contractDto.getContractId(), ps, linkDtm));
  }
  
  private void linkWithAssociatedCompany(long contractId, ContractDto.ProductSubscriptionDto productSubscriptionDto, LocalDateTime linkDtm) {
    ProductAuthId id = new ProductAuthId(contractId, productSubscriptionDto.getProductSubscriptionId());
    // DB에 있으면 아무것도 안하고, 없으면 새로운 record 생성(요청일시만 채워서)
    productAuthRepository.findById(id)
        .ifPresentOrElse(
            a -> {},
            () -> saveNewAuth(id, productSubscriptionDto.getProductCode(), linkDtm)
        );
    // eai로 보낸다고 가정
    // eaiClient.put(msg).subscribeOn(otherServiceCommScheduler);
  }
  
  private void saveNewAuth(ProductAuthId id, String productCode, LocalDateTime linkDtm) {
    productAuthRepository.save(
        ProductAuth.builder()
            .id(id)
            .productCode(productCode)
            .firstAuthReqDtm(linkDtm)
            .build()
    );
  }
  
  @Override
  @Transactional
  public void completeAuth(CompleteProductAuthDto dto) {
    productAuthRepository.findById(new ProductAuthId(dto.getContractId(), dto.getProductSubscriptionId()))
        .orElseThrow()
        .completeAuth(dto.getAuthCompletedDtm());
  }
  
  @Override
  @Transactional
  public void expireAuth(ExpireProductAuthDto dto) {
    productAuthRepository.findById(new ProductAuthId(dto.getContractId(), dto.getProductSubscriptionId()))
        .orElseThrow()
        .expireAuth(dto.getAuthExpiredDtm());
  }
  
  public List<ProductAuthDto> getProductAuths(long contractId) {
    return productAuthRepository.findByContractId(contractId)
        .stream()
        .map(ProductAuthDto::of)
        .collect(Collectors.toList());
  }
}
