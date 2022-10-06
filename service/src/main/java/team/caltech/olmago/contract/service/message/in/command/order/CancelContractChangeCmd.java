package team.caltech.olmago.contract.service.message.in.command.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CancelContractChangeCmd {
  private long orderId;
  private LocalDateTime canceledChangeReceiptDateTime;
}
