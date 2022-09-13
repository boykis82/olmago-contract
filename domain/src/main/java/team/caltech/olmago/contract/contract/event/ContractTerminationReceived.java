package team.caltech.olmago.contract.contract.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ContractTerminationReceived extends Event {
  private final long contractId;
  private final long orderId;
  private final LocalDateTime terminationReceivedDateTime;
}
