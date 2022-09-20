package team.caltech.olmago.contract.contract.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class ContractTerminationReceived extends ContractEventBase {
  private final long orderId;
  
  public ContractTerminationReceived(long contractId, LocalDateTime eventOccurDtm, long orderId) {
    super(contractId, eventOccurDtm);
    this.orderId = orderId;
  }
}
