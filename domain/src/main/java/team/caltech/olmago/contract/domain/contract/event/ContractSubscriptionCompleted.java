package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ContractSubscriptionCompleted extends ContractEventBase {
  private final List<String> productCodes;
  public ContractSubscriptionCompleted(long contractId, LocalDateTime eventOccurDtm, List<String> productCodes) {
    super(contractId, eventOccurDtm);
    this.productCodes = productCodes;
  }
}
