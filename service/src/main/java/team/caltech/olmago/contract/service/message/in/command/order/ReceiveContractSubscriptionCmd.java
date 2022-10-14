package team.caltech.olmago.contract.service.message.in.command.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ReceiveContractSubscriptionCmd {
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Product {
    String prodCd;
  }
  private long customerId;
  private long orderId;
  private String pkgProdCd;
  private String optProdCd;
  private List<Product> unitProds;
  private LocalDateTime subRcvDtm;
  
  @Builder
  public ReceiveContractSubscriptionCmd(long customerId, long orderId, String pkgProdCd, String optProdCd, List<String> unitProdCds, LocalDateTime subRcvDtm) {
    this.customerId = customerId;
    this.orderId = orderId;
    this.pkgProdCd = pkgProdCd;
    this.optProdCd = optProdCd;
    this.unitProds = unitProdCds.stream().map(Product::new).collect(Collectors.toList());
    this.subRcvDtm = subRcvDtm;
  }
  
  public boolean isPackageSubscribing() {
    return pkgProdCd != null && !pkgProdCd.isEmpty() && optProdCd != null && !optProdCd.isEmpty();
  }
  
  public boolean isUnitSubscribing() {
    return unitProds != null && unitProds.size() > 0;
  }
  
}
