package team.caltech.olmago.contract.service.message.in.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import team.caltech.olmago.contract.service.message.in.command.order.*;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class CommandDispatcher {
  private final OrderCommandHandler orderCommandHandler;
  
  @Bean
  public Consumer<Message<ReceiveContractSubscriptionCmd>> receiveContractSubscriptionCmd() {
    return m -> orderCommandHandler.receiveContractSubscription(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CancelContractSubscriptionCmd>> cancelContractSubscriptionCmd() {
    return m -> {
      // todo
    };
  }
  
  @Bean
  public Consumer<Message<ReceiveContractTerminationCmd>> receiveContractTerminationCmd() {
    return m -> orderCommandHandler.receiveContractTermination(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CancelContractTerminationCmd>> cancelContractTerminationReceiptCmd() {
    return m -> orderCommandHandler.cancelContractTerminationReceipt(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<ReceiveContractChangeCmd>> receiveContractChangeCmd() {
    return m -> orderCommandHandler.receiveContractChange(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CancelContractChangeCmd>> cancelContractChangeReceiptCmd() {
    return m -> orderCommandHandler.cancelContractChangeReceipt(m, m.getPayload());
  }
}
