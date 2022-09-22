package team.caltech.olmago.contract.message.in.event.billing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class RefundCompletedEvent {
  @RequiredArgsConstructor
  @Getter
  static class ContractRefundInfo {
    private final long contractId;
    private final long refundAmount;
  }
  
  private final long paymentId;
  private final LocalDateTime eventOccurDtm;
  private final long refundAmount;
  private final boolean isFirstPayment;
  private final List<ContractRefundInfo> ContractRefundInfos;
}
