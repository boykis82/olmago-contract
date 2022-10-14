package team.caltech.olmago.contract.productauth.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class ProductAuthBaseEvent {
  private final long contractId;
  private final String productCode;
  private final LocalDateTime eventOccurDateTime;
}
