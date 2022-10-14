package team.caltech.olmago.contract.service.message.in.event.billing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PaymentCompletedEvent {
  @NoArgsConstructor
  @Getter
  static class ContractPaymentInfo {
    private long contractId;
    private long paymentAmount;
  }
  
  private long paymentId;
  private LocalDateTime eventOccurDtm;
  private long paymentAmount;
  private boolean isFirstPayment;
  private List<ContractPaymentInfo> ContractPaymentInfos;
}
