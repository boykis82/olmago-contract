package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ContractTerminationReceiptCanceled extends ContractEventBase {
  private final long orderId;
  
  public ContractTerminationReceiptCanceled(long contractId, LocalDateTime eventOccurDtm, long orderId) {
    super(contractId, eventOccurDtm);
    this.orderId = orderId;
  }
}
