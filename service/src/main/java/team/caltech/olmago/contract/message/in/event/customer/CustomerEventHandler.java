package team.caltech.olmago.contract.message.in.event.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.dto.ChangeMobilePhoneRelatedDiscountDto;
import team.caltech.olmago.contract.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerEventHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void mobilePhoneServiceLinked(Message<MobilePhoneServiceLinkedEvent> message, MobilePhoneServiceLinkedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
  
  @Transactional
  public void mobilePhoneServiceUnlinked(Message<MobilePhoneServiceUnlinkedEvent> message, MobilePhoneServiceUnlinkedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
  
  @Transactional
  public void mobilePhonePricePlanChanged(Message<MobilePhonePricePlanChangedEvent> message, MobilePhonePricePlanChangedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
}
