package team.caltech.olmago.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.event.in.coupon.CouponUseReservedEvent;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReceiveCouponDiscountDto {
  private long contractId;
  private String couponId;
  private String couponPolicyCode;
  private LocalDateTime couponUseReservedDateTime;
  
  public static ReceiveCouponDiscountDto of(CouponUseReservedEvent event) {
    return ReceiveCouponDiscountDto.builder()
        .contractId(event.getContractId())
        .couponId(event.getCouponId())
        .couponPolicyCode(event.getCouponPolicyCode())
        .couponUseReservedDateTime(event.getCouponUseReservedDateTime())
        .build();
  }
}
