package team.caltech.olmago.contract.service.message.in.event.billing;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.service.dto.CancelContractSubscriptionDto;
import team.caltech.olmago.contract.service.dto.ActivateOrDeactivateProductDto;
import team.caltech.olmago.contract.service.dto.CompleteContractSubscriptionDto;
import team.caltech.olmago.contract.service.dto.HoldActivationDto;
import team.caltech.olmago.contract.service.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.service.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class BillingEventHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void paymentCompleted(Message<?> msg, PaymentCompletedEvent event) {
    if (!messageInBoxProcessor.notExistedMessage(msg)) {
      return;
    }
    messageInBoxProcessor.saveInBoxMessage(msg);
    if (event.isFirstPayment()) {
      event.getContractPaymentInfos().forEach(
          cp -> contractService.completeContractSubscription(new CompleteContractSubscriptionDto(cp.getContractId(), event.getEventOccurDtm()))
      );
    }
    else {
      event.getContractPaymentInfos().forEach(
          cp -> contractService.activateOrDeactivateProducts(new ActivateOrDeactivateProductDto(cp.getContractId(), event.getEventOccurDtm()))
      );
    }
  }
  
  @Transactional
  public void refundCompleted(Message<?> msg, RefundCompletedEvent event) {
    if (!messageInBoxProcessor.notExistedMessage(msg)) {
      return;
    }
    messageInBoxProcessor.saveInBoxMessage(msg);
    if (event.isFirstPayment()) {
      event.getContractRefundInfos().forEach(
          cp -> contractService.cancelContractSubscriptionReceipt(new CancelContractSubscriptionDto(cp.getContractId(), event.getEventOccurDtm()))
      );
    }
    else {
      event.getContractRefundInfos().forEach(
          cp -> contractService.holdActivation(new HoldActivationDto(cp.getContractId(), event.getEventOccurDtm()))
      );
    }
  }
}
