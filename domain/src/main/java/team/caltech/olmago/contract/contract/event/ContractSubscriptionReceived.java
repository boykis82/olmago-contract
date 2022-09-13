package team.caltech.olmago.contract.contract.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ContractSubscriptionReceived extends Event {
  private final long contractId;
  private final long orderId;
  private final LocalDateTime subscriptionReceivedDateTime;
}
