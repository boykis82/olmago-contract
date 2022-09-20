package team.caltech.olmago.contract.contract.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class ContractTerminationCompleted extends ContractEventBase {
  public ContractTerminationCompleted(long contractId, LocalDateTime eventOccurDtm) {
    super(contractId, eventOccurDtm);
  }
}
