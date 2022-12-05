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
  private LocalDateTime eventOccurDtm;
  private long mobilePhoneSvcMgmtNum;
  private String mobilePhoneNumber;
  private String mobilePhonePricePlan;
  private String dcTargetUzooPassProductCode;
}



