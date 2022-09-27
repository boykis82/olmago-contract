package team.caltech.olmago.contract.productauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CompleteProductAuthDto {
  private final long contractId;
  private final long productSubscriptionId;
  private final LocalDateTime authCompletedDtm;
}
