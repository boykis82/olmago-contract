package team.caltech.olmago.contract.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.service.message.in.event.coupon.CouponUseReleasedEvent;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReleaseCouponDiscountDto {
  private long contractId;
  private String couponId;
  private String couponPolicyCode;
  private LocalDateTime couponUseReleasedDateTime;

  public static ReleaseCouponDiscountDto of(CouponUseReleasedEvent event) {
    return ReleaseCouponDiscountDto.builder()
        .contractId(event.getContractId())
        .couponId(event.getCouponId())
        .couponPolicyCode(event.getCouponPolicyCode())
        .couponUseReleasedDateTime(event.getCouponUseReleasedDateTime())
        .build();
  }
}
