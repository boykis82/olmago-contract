package team.caltech.olmago.contract.service.message.in.command.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReceiveContractSubscriptionCmd {
  private long customerId;
  private long orderId;
  private String pkgProdCd;
  private String optProdCd;
  private List<String> unitProdCds = new ArrayList<>();
  private LocalDateTime subRcvDtm;
  
  public boolean isPackageSubscribing() {
    return pkgProdCd != null && !pkgProdCd.isEmpty() && optProdCd != null && !optProdCd.isEmpty();
  }
  
  public boolean isUnitSubscribing() {
    return unitProdCds != null && unitProdCds.size() > 0;
  }
  
}
