package team.caltech.olmago.contract.domain.contract.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class ContractEventBase {
  protected final Long contractId;
  protected final LocalDateTime eventOccurDtm;
}
