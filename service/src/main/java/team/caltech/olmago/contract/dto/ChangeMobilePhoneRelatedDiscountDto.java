package team.caltech.olmago.contract.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.customer.MobilePhonePricePlan;
import team.caltech.olmago.contract.event.in.customer.MobilePhonePricePlanChangedEvent;
import team.caltech.olmago.contract.event.in.customer.MobilePhoneServiceLinkedEvent;
import team.caltech.olmago.contract.event.in.customer.MobilePhoneServiceUnlinkedEvent;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Builder
public class ChangeMobilePhoneRelatedDiscountDto {
  private final long customerId;
  private final MobilePhonePricePlan mobilePhonePricePlan;
  private final String dcTargetUzooPassProductCode;
  private final LocalDateTime changeDateTime;
  
  public static ChangeMobilePhoneRelatedDiscountDto of(MobilePhoneServiceLinkedEvent e) {
    return new ChangeMobilePhoneRelatedDiscountDto(e.getCustomerId(), e.getMobilePhonePricePlan(), e.getDcTargetUzooPassProductCode(), e.getLinkedDtm());
  }
  
  public static ChangeMobilePhoneRelatedDiscountDto of(MobilePhoneServiceUnlinkedEvent e) {
    return new ChangeMobilePhoneRelatedDiscountDto(e.getCustomerId(), e.getMobilePhonePricePlan(), e.getDcTargetUzooPassProductCode(), e.getUnlinkedDtm());
  }
  
  public static ChangeMobilePhoneRelatedDiscountDto of(MobilePhonePricePlanChangedEvent e) {
    return new ChangeMobilePhoneRelatedDiscountDto(e.getCustomerId(), e.getMobilePhonePricePlan(), e.getDcTargetUzooPassProductCode(), e.getChangeDtm());
  }
}
