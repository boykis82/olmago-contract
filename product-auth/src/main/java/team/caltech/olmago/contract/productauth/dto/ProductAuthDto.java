package team.caltech.olmago.contract.productauth.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import team.caltech.olmago.contract.productauth.domain.ProductAuth;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductAuthDto {
  private long contractId;
  private long productSubscriptionId;
  private String productCode;
  private LocalDateTime firstAuthReqDtm;
  private LocalDateTime lastAuthCompletedDtm;
  private LocalDateTime authExpiredDtm;
  
  public static ProductAuthDto of(ProductAuth productAuth) {
    return new ProductAuthDto(
        productAuth.getId().getContractId(),
        productAuth.getId().getProductSubscriptionId(),
        productAuth.getProductCode(),
        productAuth.getFirstAuthReqDtm(),
        productAuth.getLastAuthCompletedDtm(),
        productAuth.getAuthExpiredDtm()
    );
  }
}
