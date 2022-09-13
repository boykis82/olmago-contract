package team.caltech.olmago.contract.contract.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ContractSubscriptionCompleted extends Event {
  private final long contractId;
  private final LocalDateTime subscriptionCompletedDateTime;
}
