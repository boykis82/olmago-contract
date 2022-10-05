package team.caltech.olmago.contract.service.dto;

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
public class ReceiveContractSubscriptionDto {
  private long customerId;
  private long orderId;
  private String pkgProdCd;
  private String optProdCd;
  private List<String> unitProdCds = new ArrayList<>();
  private LocalDateTime subRcvDtm;
}
