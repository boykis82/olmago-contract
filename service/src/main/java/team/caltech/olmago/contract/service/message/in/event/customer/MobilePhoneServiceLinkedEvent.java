package team.caltech.olmago.contract.service.message.in.event.customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.domain.customer.MobilePhonePricePlan;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class MobilePhoneServiceLinkedEvent {
  private final long customerId;
  private final LocalDateTime linkedDtm;
  private final long mobilePhoneSvcMgmtNum;
  private final String mobilePhoneNumber;
  private final MobilePhonePricePlan mobilePhonePricePlan;
  private final String dcTargetUzooPassProductCode;
}


