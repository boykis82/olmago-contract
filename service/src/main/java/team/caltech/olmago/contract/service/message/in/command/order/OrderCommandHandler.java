package team.caltech.olmago.contract.service.message.in.command.order;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.service.dto.*;
import team.caltech.olmago.contract.service.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class OrderCommandHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void receiveContractSubscription(Message<?> msg, ReceiveContractSubscriptionDto dto) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.receiveContractSubscription(dto);
    }
  }
  
  @Transactional
  public void receiveContractTermination(Message<?> msg, ReceiveContractTerminationDto dto) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.receiveContractTermination(dto);
    }
  }
  
  @Transactional
  public void cancelContractTerminationReceipt(Message<?> msg, CancelContractTerminationDto dto) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.cancelContractTerminationReceipt(dto);
    }
  }
  
  @Transactional
  public void receiveContractChange(Message<?> msg, ReceiveContractChangeDto dto) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.receiveContractChange(dto);
    }
  }
  
  @Transactional
  public void cancelContractChangeReceipt(Message<?> msg, CancelContractChangeDto dto) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.cancelContractChangeReceipt(dto);
    }
  }
}
