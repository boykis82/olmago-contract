package team.caltech.olmago.contract.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.customer.MobilePhonePricePlan;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Builder
public class ChangeMobilePhoneRelatedDiscountDto {
  private final long customerId;
  private final MobilePhonePricePlan mobilePhonePricePlan;
  private final String dcTargetUzooPassProductCode;
  private final LocalDateTime changeDateTime;
}
