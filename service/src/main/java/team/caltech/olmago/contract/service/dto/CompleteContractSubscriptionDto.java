package team.caltech.olmago.contract.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CompleteContractSubscriptionDto {
  private long contractId;
  private LocalDateTime subscriptionCompletedDateTime;
}
