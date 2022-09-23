package team.caltech.olmago.contract.service.message.in.event.billing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PaymentCompletedEvent {
  @RequiredArgsConstructor
  @Getter
  static class ContractPaymentInfo {
    private final long contractId;
    private final long paymentAmount;
  }
  
  private final long paymentId;
  private final LocalDateTime eventOccurDtm;
  private final long paymentAmount;
  private final boolean isFirstPayment;
  private final List<ContractPaymentInfo> ContractPaymentInfos;
}
