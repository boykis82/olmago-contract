package team.caltech.olmago.contract.event.consumer.customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.customer.MobilePhonePricePlan;

@RequiredArgsConstructor
@Getter
public class MobilePhonePricePlanChangedEvent {
  private final long customerId;
  private final long mobilePhoneSvcMgmtNum;
  private final String mobilePhoneNumber;
  private final MobilePhonePricePlan mobilePhonePricePlan;
  private final String dcTargetUzooPassProductCode;
}



