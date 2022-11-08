package team.caltech.olmago.contract.productauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.caltech.olmago.common.message.MessageEnvelope;
import team.caltech.olmago.contract.productauth.domain.ProductAuth;
import team.caltech.olmago.contract.productauth.domain.ProductAuthId;
import team.caltech.olmago.contract.productauth.domain.ProductAuthRepository;
import team.caltech.olmago.contract.productauth.domain.event.ProductAuthBaseEvent;
import team.caltech.olmago.contract.productauth.dto.CompleteProductAuthDto;
import team.caltech.olmago.contract.productauth.dto.ExpireProductAuthDto;
import team.caltech.olmago.contract.productauth.dto.ProductAuthDto;
import team.caltech.olmago.contract.productauth.message.out.MessagePublisher;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductAuthServiceImpl implements ProductAuthService {
  private final ProductAuthRepository productAuthRepository;
  
  //private final EAIClient eaiClient;
  
  private final MessagePublisher messagePublisher;
  private final ObjectMapper objectMapper;
  
  public static final String AGGREGATE_TYPE = "PRODUCT_AUTH";
  public static final String EVENT_BINDING = "product-auth-event-0";
  
  @Override
  @Transactional
  public void linkWithAssociatedCompanies(long contractId, List<String> productCodes, LocalDateTime linkDtm) {
    productCodes.forEach(productCode -> linkWithAssociatedCompany(contractId, productCode, linkDtm));
  }
  
  private void linkWithAssociatedCompany(long contractId, String productCode, LocalDateTime linkDtm) {
    ProductAuthId id = new ProductAuthId(contractId, productCode);
    // DB에 있으면 아무것도 안하고, 없으면 새로운 record 생성(요청일시만 채워서)
    productAuthRepository.findById(id)
        .ifPresentOrElse(
            a -> {},
            () -> saveNewAuth(id, productCode, linkDtm)
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
    ProductAuthBaseEvent event = productAuthRepository.findById(new ProductAuthId(dto.getContractId(), dto.getProductCode())).orElseThrow()
        .completeAuth(dto.getAuthCompletedDtm());
    messagePublisher.sendMessage(wrapEvent(event));
  }
  
  @Override
  @Transactional
  public void expireAuth(ExpireProductAuthDto dto) {
    productAuthRepository.findById(new ProductAuthId(dto.getContractId(), dto.getProductCode()))
        .orElseThrow()
        .expireAuth(dto.getAuthExpiredDtm());
  }
  
  public List<ProductAuthDto> getProductAuths(long contractId) {
    return productAuthRepository.findByContractId(contractId)
        .stream()
        .map(ProductAuthDto::of)
        .collect(Collectors.toList());
  }
  
  private MessageEnvelope wrapEvent(ProductAuthBaseEvent e) {
    try {
      return MessageEnvelope.wrapEvent(
          AGGREGATE_TYPE,
          String.valueOf(e.getContractId()) + e.getProductCode(),
          EVENT_BINDING,
          e.getClass().getSimpleName(),
          objectMapper.writeValueAsString(e)
      );
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
  }
}
