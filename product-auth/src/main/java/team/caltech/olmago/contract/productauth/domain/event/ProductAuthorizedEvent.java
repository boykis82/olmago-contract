package team.caltech.olmago.contract.productauth.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductAuthorizedEvent extends ProductAuthBaseEvent {
  public ProductAuthorizedEvent(long contractId, String productCode, LocalDateTime eventOccurDateTime) {
    super(contractId, productCode, eventOccurDateTime);
  }
}
