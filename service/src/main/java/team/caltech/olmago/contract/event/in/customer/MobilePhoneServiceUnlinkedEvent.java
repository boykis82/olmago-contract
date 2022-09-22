package team.caltech.olmago.contract.event.in.customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.customer.MobilePhonePricePlan;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class MobilePhoneServiceUnlinkedEvent {
  private final long customerId;
  private final LocalDateTime unlinkedDtm;
  private final long mobilePhoneSvcMgmtNum;
  private final String mobilePhoneNumber;
  private final MobilePhonePricePlan mobilePhonePricePlan;
  private final String dcTargetUzooPassProductCode;
}



