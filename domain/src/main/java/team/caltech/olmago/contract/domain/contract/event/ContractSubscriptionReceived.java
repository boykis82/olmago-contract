package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ContractSubscriptionReceived extends ContractEventBase {
  private final long orderId;
  private final String feeProductCode;
  
  public ContractSubscriptionReceived(long contractId, LocalDateTime eventOccurDtm, long orderId, String feeProductCode) {
    super(contractId, eventOccurDtm);
    this.orderId = orderId;
    this.feeProductCode = feeProductCode;
  }
}
