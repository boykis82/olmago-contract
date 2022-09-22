package team.caltech.olmago.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CancelContractSubscriptionDto {
  private long contractId;
  private LocalDateTime subscriptionCanceledDateTime;
}
