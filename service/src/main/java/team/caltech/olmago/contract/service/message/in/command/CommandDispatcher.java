package team.caltech.olmago.contract.service.message.in.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import team.caltech.olmago.contract.service.message.in.command.order.*;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Configuration
public class CommandDispatcher {
  private final OrderCommandHandler orderCommandHandler;
  
  @Bean
  public Consumer<Message<ReceiveContractSubscriptionCmd>> receiveContractSubscription() {
    return m -> orderCommandHandler.receiveContractSubscription(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<ReceiveContractTerminationCmd>> receiveContractTermination() {
    return m -> orderCommandHandler.receiveContractTermination(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CancelContractTerminationCmd>> cancelContractTerminationReceipt() {
    return m -> orderCommandHandler.cancelContractTerminationReceipt(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<ReceiveContractChangeCmd>> receiveContractChange() {
    return m -> orderCommandHandler.receiveContractChange(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CancelContractChangeCmd>> cancelContractChangeReceipt() {
    return m -> orderCommandHandler.cancelContractChangeReceipt(m, m.getPayload());
  }
}
