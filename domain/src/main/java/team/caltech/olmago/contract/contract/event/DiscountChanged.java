package team.caltech.olmago.contract.contract.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class DiscountChanged extends Event {
  private final long contractId;
  private final LocalDateTime dcChangedDateTime;
}
