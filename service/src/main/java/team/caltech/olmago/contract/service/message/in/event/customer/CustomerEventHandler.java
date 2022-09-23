package team.caltech.olmago.contract.service.message.in.event.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.service.dto.ChangeMobilePhoneRelatedDiscountDto;
import team.caltech.olmago.contract.service.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.service.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerEventHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void mobilePhoneServiceLinked(Message<?> message, MobilePhoneServiceLinkedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
  
  @Transactional
  public void mobilePhoneServiceUnlinked(Message<?> message, MobilePhoneServiceUnlinkedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
  
  @Transactional
  public void mobilePhonePricePlanChanged(Message<?> message, MobilePhonePricePlanChangedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
}
