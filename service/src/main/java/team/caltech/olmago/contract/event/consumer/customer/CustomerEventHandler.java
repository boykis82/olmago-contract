package team.caltech.olmago.contract.event.consumer.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.caltech.olmago.contract.customer.MobilePhonePricePlan;
import team.caltech.olmago.contract.dto.ChangeMobilePhoneRelatedDiscountDto;
import team.caltech.olmago.contract.service.ContractService;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Configuration
public class CustomerEventHandler {
  private final ContractService contractService;

  @Bean
  public Consumer<MobilePhoneServiceLinkedEvent> mobilePhoneServiceLinked() {
    return e -> changeMobilePhoneRelatedDiscount(e.getCustomerId(), e.getMobilePhonePricePlan(), e.getDcTargetUzooPassProductCode());
  }

  @Bean
  public Consumer<MobilePhoneServiceUnlinkedEvent> mobilePhoneServiceUnlinked() {
    return e -> changeMobilePhoneRelatedDiscount(e.getCustomerId(), e.getMobilePhonePricePlan(), e.getDcTargetUzooPassProductCode());
  }

  @Bean
  public Consumer<MobilePhonePricePlanChangedEvent> mobilePhonePricePlanChanged() {
    return e -> changeMobilePhoneRelatedDiscount(e.getCustomerId(), e.getMobilePhonePricePlan(), e.getDcTargetUzooPassProductCode());
  }

  private void changeMobilePhoneRelatedDiscount(long customerId, MobilePhonePricePlan mobilePhonePricePlan, String uzoopassProductCode) {
    ChangeMobilePhoneRelatedDiscountDto dto = ChangeMobilePhoneRelatedDiscountDto.builder()
        .customerId(customerId)
        .mobilePhonePricePlan(mobilePhonePricePlan)
        .dcTargetUzooPassProductCode(uzoopassProductCode)
        .build();
    contractService.changeMobilePhoneRelatedDiscount(dto);
  }
}
