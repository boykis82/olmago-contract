package team.caltech.olmago.contract.proxy.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.proxy.customer.MobilePhonePricePlan;

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
}
