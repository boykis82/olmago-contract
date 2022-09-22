package team.caltech.olmago.contract.event.in.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.dto.ChangeMobilePhoneRelatedDiscountDto;
import team.caltech.olmago.contract.event.in.common.EventInBoxProcessor;
import team.caltech.olmago.contract.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerEventHandler {
  private final EventInBoxProcessor eventInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void mobilePhoneServiceLinked(Message<MobilePhoneServiceLinkedEvent> message, MobilePhoneServiceLinkedEvent event) {
    if (eventInBoxProcessor.notExistedEvent(message)) {
      eventInBoxProcessor.saveInBoxEvent(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
  
  @Transactional
  public void mobilePhoneServiceUnlinked(Message<MobilePhoneServiceUnlinkedEvent> message, MobilePhoneServiceUnlinkedEvent event) {
    if (eventInBoxProcessor.notExistedEvent(message)) {
      eventInBoxProcessor.saveInBoxEvent(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
  
  @Transactional
  public void mobilePhonePricePlanChanged(Message<MobilePhonePricePlanChangedEvent> message, MobilePhonePricePlanChangedEvent event) {
    if (eventInBoxProcessor.notExistedEvent(message)) {
      eventInBoxProcessor.saveInBoxEvent(message);
      contractService.changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto.of(event));
    }
  }
}
