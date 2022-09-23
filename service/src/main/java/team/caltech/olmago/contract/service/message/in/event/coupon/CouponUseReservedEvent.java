package team.caltech.olmago.contract.service.message.in.event.coupon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CouponUseReservedEvent {
  private final long contractId;
  private final String couponId;
  private final String couponPolicyCode;
  private final LocalDateTime couponUseReservedDateTime;
}
