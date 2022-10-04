package team.caltech.olmago.contract.productauth.message.in.event.contract;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductsActivatedOrDeactivated {
  private final long contractId;
  private final LocalDateTime eventOccurDtm;
  private final List<String> subProductCodes;
  private final List<String> termProductCodes;
}