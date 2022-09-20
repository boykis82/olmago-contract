package team.caltech.olmago.contract.contract.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RegularPaymentCompleted extends ContractEventBase {
  private final List<String> subProductIds;
  private final List<String> termProductIds;
  
  public RegularPaymentCompleted(long contractId, LocalDateTime eventOccurDtm, List<String> subProductIds, List<String> termProductIds) {
    super(contractId, eventOccurDtm);
    this.subProductIds = subProductIds;
    this.termProductIds = termProductIds;
  }
}
