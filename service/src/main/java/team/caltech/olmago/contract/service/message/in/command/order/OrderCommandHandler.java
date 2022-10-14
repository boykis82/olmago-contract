package team.caltech.olmago.contract.service.message.in.command.order;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.service.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.service.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class OrderCommandHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void receiveContractSubscription(Message<?> msg, ReceiveContractSubscriptionCmd cmd) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.receiveContractSubscription(cmd);
    }
  }

  @Transactional
  public void cancelContractSubscription(Message<?> msg, CancelContractSubscriptionCmd cmd) {
    /*
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.cancelContractSubscriptionReceipt(cmd);
    }
     */
  }
  
  @Transactional
  public void receiveContractTermination(Message<?> msg, ReceiveContractTerminationCmd cmd) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.receiveContractTermination(cmd);
    }
  }
  
  @Transactional
  public void cancelContractTerminationReceipt(Message<?> msg, CancelContractTerminationCmd cmd) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.cancelContractTerminationReceipt(cmd);
    }
  }
  
  @Transactional
  public void receiveContractChange(Message<?> msg, ReceiveContractChangeCmd cmd) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.receiveContractChange(cmd);
    }
  }
  
  @Transactional
  public void cancelContractChangeReceipt(Message<?> msg, CancelContractChangeCmd cmd) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.cancelContractChangeReceipt(cmd);
    }
  }
}
