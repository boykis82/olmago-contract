package team.caltech.olmago.contract.productauth.message.in.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import team.caltech.olmago.contract.productauth.message.in.event.contract.ContractEventHandler;
import team.caltech.olmago.contract.productauth.message.in.event.contract.ContractSubscriptionCompleted;
import team.caltech.olmago.contract.productauth.message.in.event.contract.ContractSubscriptionReceiptCanceled;
import team.caltech.olmago.contract.productauth.message.in.event.contract.ProductsActivatedOrDeactivated;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Configuration
public class EventConsumer {
  private final ContractEventHandler contractEventHandler;

  @Bean
  public Consumer<Message<ContractSubscriptionCompleted>> contractSubscriptionCompleted() {
    return m -> contractEventHandler.contractSubscriptionCompleted(m, m.getPayload());
  }

  @Bean
  public Consumer<Message<ContractSubscriptionReceiptCanceled>> contractSubscriptionReceiptCanceled() {
    return m -> contractEventHandler.contractSubscriptionReceiptCanceled(m, m.getPayload());
  }

  @Bean
  public Consumer<Message<ProductsActivatedOrDeactivated>> productActivatedOrDeactivated() {
    return m -> contractEventHandler.productActivatedOrDeactivated(m, m.getPayload());
  }

}
