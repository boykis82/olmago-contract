package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ContractTerminationCompleted extends ContractEventBase {
  public ContractTerminationCompleted(long contractId, LocalDateTime eventOccurDtm) {
    super(contractId, eventOccurDtm);
  }
}
