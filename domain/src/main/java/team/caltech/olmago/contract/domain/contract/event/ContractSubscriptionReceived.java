package team.caltech.olmago.contract.domain.contract.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ContractSubscriptionReceived extends ContractEventBase {
  @Getter
  @AllArgsConstructor
  public static class Product {
    Long id;
    String productCode;
    List<Discount> discounts;
    
    @Getter
    @AllArgsConstructor
    public static class Discount {
      Long id;
      String discountPolicyCode;
    }
  }
  
  private final long orderId;
  private final String feeProductCode;
  private final List<Product> products;
  
  public ContractSubscriptionReceived(Long contractId,
                                      LocalDateTime eventOccurDtm,
                                      long orderId,
                                      String feeProductCode,
                                      List<ContractSubscriptionReceived.Product> products) {
    super(contractId, eventOccurDtm);
    this.orderId = orderId;
    this.feeProductCode = feeProductCode;
    this.products = products;
  }
}
