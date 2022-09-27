package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProductsActivatedOrDeactivated extends ContractEventBase {

  public ProductsActivatedOrDeactivated(long contractId, LocalDateTime eventOccurDtm) {
    super(contractId, eventOccurDtm);
  }
}
