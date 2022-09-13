package team.caltech.olmago.contract.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LinkedMobilePhoneInfo {
  private long customerId;
  private long mobilePhoneSvcMgmtNum;
  private String mobilePhoneNumber;
  private MobilePhonePricePlan mobilePhonePricePlan;
  private String dcTargetUzooPassProductCode;
  
  public boolean matchMobilePhonePricePlanAndUzooPassProductCode(MobilePhonePricePlan mobilePhonePricePlan, String uzooPassProductCode) {
    return this.mobilePhonePricePlan.equals(mobilePhonePricePlan) &&
        this.dcTargetUzooPassProductCode.equals(uzooPassProductCode);
  }
}
