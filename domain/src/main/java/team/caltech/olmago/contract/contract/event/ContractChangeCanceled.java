package team.caltech.olmago.contract.contract.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;

@Getter
public class ContractChangeCanceled extends ContractEventBase {
  private final long orderId;
  
  public ContractChangeCanceled(long contractId, LocalDateTime eventOccurDtm, long orderId) {
    super(contractId, eventOccurDtm);
    this.orderId = orderId;
  }
}
