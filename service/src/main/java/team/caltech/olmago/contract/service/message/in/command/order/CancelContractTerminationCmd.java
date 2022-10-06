package team.caltech.olmago.contract.service.message.in.command.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CancelContractTerminationCmd {
  private long orderId;
  private LocalDateTime cancelTerminationReceiptDateTime;
  private Long packageContractId;
  private Long optionContractId;
  private List<Long> unitContractIds;
  
  public boolean includePackage() {
    return packageContractId != null && optionContractId != null;
  }
}
