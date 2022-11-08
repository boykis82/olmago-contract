package team.caltech.olmago.contract.service.message.in.event.productauth;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.service.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.service.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class ProductAuthEventHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void productAuthorized(Message<?> message, ProductAuthorizedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      contractService.markProductAuthorizedDateTime(event.getContractId(), event.getProductCode(), event.getEventOccurDtm());
    }
  }
  
}
