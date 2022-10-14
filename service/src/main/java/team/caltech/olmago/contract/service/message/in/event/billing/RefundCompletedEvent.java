package team.caltech.olmago.contract.service.message.in.event.billing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class RefundCompletedEvent {
  @NoArgsConstructor
  @Getter
  static class ContractRefundInfo {
    private long contractId;
    private long refundAmount;
  }
  
  private long paymentId;
  private LocalDateTime eventOccurDtm;
  private long refundAmount;
  private boolean isFirstPayment;
  private List<ContractRefundInfo> ContractRefundInfos;
}
