package team.caltech.olmago.contract.domain.product;

import lombok.Builder;
import lombok.Getter;
import team.caltech.olmago.contract.domain.discount.DiscountCalculationResult;

import java.util.List;

@Getter
public class ProductCalculationResult {
  private final Long productSubscriptionId;
  private final String productCode;
  private final long prodAmountIncludeVat;
  private final List<DiscountCalculationResult> discountCalculationResults;
  
  @Builder
  public ProductCalculationResult(Long productSubscriptionId, String productCode, long prodAmountIncludeVat, List<DiscountCalculationResult> discountCalculationResults) {
    this.productSubscriptionId = productSubscriptionId;
    this.productCode = productCode;
    this.prodAmountIncludeVat = prodAmountIncludeVat;
    this.discountCalculationResults = discountCalculationResults;
  }
  
  public DiscountCalculationResult getDiscountCalculationResult(String dcPolicyId) {
    return discountCalculationResults.stream()
        .filter(pc -> pc.getDcPolicyCode().equals(dcPolicyId))
        .findAny()
        .orElseThrow(IllegalStateException::new);
  }
}
