package team.caltech.olmago.contract.contract.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ContractEventBase {
  protected final long contractId;
  protected final LocalDateTime eventOccurDtm;
}
