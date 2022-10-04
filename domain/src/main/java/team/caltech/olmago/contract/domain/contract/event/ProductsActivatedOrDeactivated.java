package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProductsActivatedOrDeactivated extends ContractEventBase {
  private final List<String> subProductCodes;
  private final List<String> termProductCodes;
  
  public ProductsActivatedOrDeactivated(long contractId, LocalDateTime eventOccurDtm, List<String> subProductCodes, List<String> termProductCodes) {
    super(contractId, eventOccurDtm);
    this.subProductCodes = subProductCodes;
    this.termProductCodes = termProductCodes;
  }
}
