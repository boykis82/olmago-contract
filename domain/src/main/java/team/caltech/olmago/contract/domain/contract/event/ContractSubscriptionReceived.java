package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ContractSubscriptionReceived extends ContractEventBase {
  private final long orderId;
  private final String feeProductCode;
  private final List<String> productCodes;
  
  public ContractSubscriptionReceived(long contractId, LocalDateTime eventOccurDtm, long orderId, String feeProductCode, List<String> productCodes) {
    super(contractId, eventOccurDtm);
    this.orderId = orderId;
    this.feeProductCode = feeProductCode;
    this.productCodes = productCodes;
  }
}
