package team.caltech.olmago.contract.productauth.message.in.event.contract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductsActivatedOrDeactivated {
  private long contractId;
  private LocalDateTime eventOccurDtm;
  private List<String> subProductCodes;
  private List<String> termProductCodes;
}