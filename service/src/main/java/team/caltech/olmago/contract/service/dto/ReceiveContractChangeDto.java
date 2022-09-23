package team.caltech.olmago.contract.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReceiveContractChangeDto {
  private long orderId;
  private long customerId;

  private LocalDateTime changeReceivedDateTime;

  private Long packageContractId;
  private String beforePackageProductCode;
  private String afterPackageProductCode;

  private Long optionContractId;
  private String beforeOptionProductCode;
  private String afterOptionProductCode;
  private boolean keepingBeforeContract;

  public boolean isChangingBothContract() {
    return isChangingOptionContract() && isChangingPackageContract();
  }

  public boolean isChangingPackageContract() {
    return afterPackageProductCode != null;
  }

  public boolean isChangingOptionContract() {
    return afterOptionProductCode != null;
  }
}
