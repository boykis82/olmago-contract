package team.caltech.olmago.contract.service.message.in.event.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponUseReleasedEvent {
  private long contractId;
  private String couponId;
  private String couponPolicyCode;
  private LocalDateTime couponUseReleasedDateTime;
}
