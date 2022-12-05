package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ContractSubscriptionCompleted extends ContractEventBase {
  private final List<String> productCodes;
  private final long orderId;
  public ContractSubscriptionCompleted(Long contractId, LocalDateTime eventOccurDtm, long orderId, List<String> productCodes) {
    super(contractId, eventOccurDtm);
    this.orderId = orderId;
    this.productCodes = productCodes;
  }
}
