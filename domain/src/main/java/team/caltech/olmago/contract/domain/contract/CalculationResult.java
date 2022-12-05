package team.caltech.olmago.contract.domain.contract;

import lombok.Builder;
import lombok.Getter;
import team.caltech.olmago.contract.domain.product.ProductCalculationResult;

import java.util.List;

@Getter
public class CalculationResult {
  private final Long contractId;
  private final String contractType;
  private final List<ProductCalculationResult> productCalculationResults;
  
  @Builder
  public CalculationResult(Long contractId, String contractType, List<ProductCalculationResult> productCalculationResults) {
    this.contractId = contractId;
    this.contractType = contractType;
    this.productCalculationResults = productCalculationResults;
  }
  
  public ProductCalculationResult getProductCalculationResult(String productCode) {
    return productCalculationResults.stream()
        .filter(pc -> pc.getProductCode().equals(productCode))
        .findAny()
        .orElse(null);
  }
}