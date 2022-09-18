package team.caltech.olmago.contract.event.consumer.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.caltech.olmago.contract.service.ContractService;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Configuration
public class CouponEventHandler {
  private final ContractService contractService;

  @Bean
  public Consumer<CouponUseReservedEvent> couponUseReserved() {
    return e -> {
      //
    };
  }

  @Bean
  public Consumer<CouponUseReleasedEvent> couponUseReserveationReleased() {
    return e -> {
      //
    };
  }
}
