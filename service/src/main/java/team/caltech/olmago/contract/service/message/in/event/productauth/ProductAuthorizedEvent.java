package team.caltech.olmago.contract.service.message.in.event.productauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ProductAuthorizedEvent {
  private long contractId;
  private LocalDateTime eventOccurDtm;
  private String productCode;
}
