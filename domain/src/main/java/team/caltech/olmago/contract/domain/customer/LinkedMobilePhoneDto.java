package team.caltech.olmago.contract.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LinkedMobilePhoneDto {
  private long customerId;
  private long mobilePhoneSvcMgmtNum;
  private String mobilePhoneNumber;
  private MobilePhonePricePlan mobilePhonePricePlan;
  private String dcTargetUzooPassProductCode;
  
  public boolean matchMobilePhonePricePlanAndUzooPassProductCode(
      MobilePhonePricePlan mobilePhonePricePlan,
      String uzooPassProductCode
  ) {
    return mobilePhonePricePlan.equals(this.mobilePhonePricePlan) &&
        uzooPassProductCode.equals(this.dcTargetUzooPassProductCode);
  }
}
