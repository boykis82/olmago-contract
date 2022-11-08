package team.caltech.olmago.contract.service.message.in.event.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponUseReleasedEvent {
  private String couponId;
  private LocalDateTime eventOccurDtm;
  private long contractId;
  private String couponPolicyCode;
}
