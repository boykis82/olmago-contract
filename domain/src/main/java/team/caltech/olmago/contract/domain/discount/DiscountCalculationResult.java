package team.caltech.olmago.contract.domain.discount;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiscountCalculationResult {
  private final Long discountSubscriptionId;
  private final String dcPolicyCode;
  private final long dcAmountIncludeVat;
  
  @Builder
  public DiscountCalculationResult(Long discountSubscriptionId, String dcPolicyCode, long dcAmountIncludeVat) {
    this.discountSubscriptionId = discountSubscriptionId;
    this.dcPolicyCode = dcPolicyCode;
    this.dcAmountIncludeVat = dcAmountIncludeVat;
  }
}
