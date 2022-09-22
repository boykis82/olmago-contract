package team.caltech.olmago.contract.message.in.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import team.caltech.olmago.contract.dto.*;
import team.caltech.olmago.contract.message.in.command.order.OrderCommandHandler;
import team.caltech.olmago.contract.message.in.event.customer.CustomerEventHandler;
import team.caltech.olmago.contract.message.in.event.customer.MobilePhoneServiceLinkedEvent;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Configuration
public class CommandDispatcher {
  private final OrderCommandHandler orderCommandHandler;
  
  @Bean
  public Consumer<Message<ReceiveContractSubscriptionDto>> receiveContractSubscription() {
    return m -> orderCommandHandler.receiveContractSubscription(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<ReceiveContractTerminationDto>> receiveContractTermination() {
    return m -> orderCommandHandler.receiveContractTermination(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CancelContractTerminationDto>> cancelContractTerminationReceipt() {
    return m -> orderCommandHandler.cancelContractTerminationReceipt(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<ReceiveContractChangeDto>> receiveContractChange() {
    return m -> orderCommandHandler.receiveContractChange(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CancelContractChangeDto>> cancelContractChangeReceipt() {
    return m -> orderCommandHandler.cancelContractChangeReceipt(m, m.getPayload());
  }
}
