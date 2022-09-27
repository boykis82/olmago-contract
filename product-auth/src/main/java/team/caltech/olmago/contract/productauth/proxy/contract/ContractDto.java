package team.caltech.olmago.contract.productauth.proxy.contract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class ContractDto {
  @Getter
  public static class ProductSubscriptionDto {
    private long productSubscriptionId;
    private String productCode;
    private LocalDateTime subscriptionReceivedDateTime;
    private LocalDateTime subscriptionCompletedDateTime;
    private LocalDateTime terminationReceivedDateTime;
    private LocalDateTime cancelTerminationReceiptDateTime;
    private LocalDateTime terminationCompletedDateTime;
    
    public boolean isActive() {
      return subscriptionReceivedDateTime != null &&
          subscriptionCompletedDateTime != null &&
          terminationCompletedDateTime == null &&
          terminationReceivedDateTime == null;
    }
  }
  
  private long contractId;
  private long customerId;
  private LocalDateTime subscriptionReceivedDateTime;
  private LocalDateTime subscriptionCompletedDateTime;
  private LocalDateTime terminationReceivedDateTime;
  private LocalDateTime cancelTerminationReceiptDateTime;
  private LocalDateTime terminationCompletedDateTime;
  private List<ProductSubscriptionDto> productSubscriptions;
  
}
