package team.caltech.olmago.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.contract.ContractType;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReceiveContractSubscriptionDto {
  private long customerId;
  private long orderId;
  private String pkgProdCd;
  private String optProdCd;
  private List<String> unitProdCds;
  private LocalDateTime subRcvDtm;
}
