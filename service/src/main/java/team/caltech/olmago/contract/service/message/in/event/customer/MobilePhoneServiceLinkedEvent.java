package team.caltech.olmago.contract.service.message.in.event.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.domain.customer.MobilePhonePricePlan;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class MobilePhoneServiceLinkedEvent {
  private long customerId;
  private LocalDateTime linkedDtm;
  private long mobilePhoneSvcMgmtNum;
  private String mobilePhoneNumber;
  private MobilePhonePricePlan mobilePhonePricePlan;
  private String dcTargetUzooPassProductCode;
}



