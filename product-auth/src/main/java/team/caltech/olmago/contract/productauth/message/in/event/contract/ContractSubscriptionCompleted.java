package team.caltech.olmago.contract.productauth.message.in.event.contract;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ContractSubscriptionCompleted {
  private final long contractId;
  private final LocalDateTime eventOccurDtm;
}
