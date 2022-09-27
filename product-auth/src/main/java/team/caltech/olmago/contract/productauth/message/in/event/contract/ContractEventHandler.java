package team.caltech.olmago.contract.productauth.message.in.event.contract;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.productauth.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.productauth.proxy.contract.ContractDto;
import team.caltech.olmago.contract.productauth.proxy.contract.ContractServiceProxy;
import team.caltech.olmago.contract.productauth.service.ProductAuthService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ContractEventHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ProductAuthService productAuthService;
  
  private final ContractServiceProxy contractServiceProxy;
  
  public void contractSubscriptionCompleted(Message<?> message, ContractSubscriptionCompleted event) {
    contractServiceProxy.getContract(event.getContractId())
        .doOnNext(c -> linkWithAssociatedCompanies(message, c, event.getEventOccurDtm()))
        .subscribe();
  }
  
  public void contractSubscriptionReceiptCanceled(Message<?> message, ContractSubscriptionReceiptCanceled event) {
    contractServiceProxy.getContract(event.getContractId())
        .doOnNext(c -> linkWithAssociatedCompanies(message, c, event.getEventOccurDtm()))
        .subscribe();
  }

  public void productActivatedOrDeactivated(Message<?> message, ProductsActivatedOrDeactivated event) {
    contractServiceProxy.getContract(event.getContractId())
        .doOnNext(c -> linkWithAssociatedCompanies(message, c, event.getEventOccurDtm()))
        .subscribe();
  }
  
  @Transactional
  private void linkWithAssociatedCompanies(Message<?> message, ContractDto contractDto, LocalDateTime eventOccurDtm) {
    if (messageInBoxProcessor.notExistedMessage(message)) {
      messageInBoxProcessor.saveInBoxMessage(message);
      productAuthService.linkWithAssociatedCompanies(contractDto, eventOccurDtm);
    }
  }
}
