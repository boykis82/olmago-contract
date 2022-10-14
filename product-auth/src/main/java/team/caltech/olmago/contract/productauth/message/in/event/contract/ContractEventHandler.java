package team.caltech.olmago.contract.productauth.message.in.event.contract;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.productauth.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.productauth.service.ProductAuthService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ContractEventHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ProductAuthService productAuthService;
  
  /* 계약 가입 완료 시 발생하는 이벤트 구독하여 제휴사와 연결 */
  @Transactional
  public void contractSubscriptionCompleted(Message<?> message, ContractSubscriptionCompleted event) {
    linkWithAssociatedCompanies(message, event.getContractId(), event.getProductCodes(), event.getEventOccurDtm());
  }

  /* 가입 상품 변경하여 신규 상품 활성화, 기존 상품 인증 비활성화 등 처리 */
  @Transactional
  public void productActivatedOrDeactivated(Message<?> message, ProductsActivatedOrDeactivated event) {
    linkWithAssociatedCompanies(message, event.getContractId(), event.getSubProductCodes(), event.getEventOccurDtm());
  }
  
  private void linkWithAssociatedCompanies(Message<?> message, long contractId, List<String> productCodes, LocalDateTime eventOccurDtm) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      productAuthService.linkWithAssociatedCompanies(contractId, productCodes, eventOccurDtm);
    }
  }
}
