package team.caltech.olmago.contract.service.message.in.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import team.caltech.olmago.contract.service.message.in.event.billing.BillingEventHandler;
import team.caltech.olmago.contract.service.message.in.event.billing.PaymentCompletedEvent;
import team.caltech.olmago.contract.service.message.in.event.billing.RefundCompletedEvent;
import team.caltech.olmago.contract.service.message.in.event.coupon.CouponEventHandler;
import team.caltech.olmago.contract.service.message.in.event.coupon.CouponUseReleasedEvent;
import team.caltech.olmago.contract.service.message.in.event.coupon.CouponUseReservedEvent;
import team.caltech.olmago.contract.service.message.in.event.customer.CustomerEventHandler;
import team.caltech.olmago.contract.service.message.in.event.customer.MobilePhonePricePlanChangedEvent;
import team.caltech.olmago.contract.service.message.in.event.customer.MobilePhoneServiceLinkedEvent;
import team.caltech.olmago.contract.service.message.in.event.customer.MobilePhoneServiceUnlinkedEvent;
import team.caltech.olmago.contract.service.message.in.event.productauth.ProductAuthEventHandler;
import team.caltech.olmago.contract.service.message.in.event.productauth.ProductAuthorizedEvent;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Configuration
public class EventConsumer {
  private final CustomerEventHandler customerEventHandler;
  private final CouponEventHandler couponEventHandler;
  private final BillingEventHandler billingEventHandler;
  private final ProductAuthEventHandler productAuthEventHandler;

  @Bean
  public Consumer<Message<MobilePhoneServiceLinkedEvent>> mobilePhoneServiceLinkedEvent() {
    return m -> customerEventHandler.mobilePhoneServiceLinked(m, m.getPayload());
  }

  @Bean
  public Consumer<Message<MobilePhoneServiceUnlinkedEvent>> mobilePhoneServiceUnlinkedEvent() {
    return m -> customerEventHandler.mobilePhoneServiceUnlinked(m, m.getPayload());
  }

  @Bean
  public Consumer<Message<MobilePhonePricePlanChangedEvent>> mobilePhonePricePlanChangedEvent() {
    return m -> customerEventHandler.mobilePhonePricePlanChanged(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CouponUseReservedEvent>> couponUseReservedEvent() {
    return m -> couponEventHandler.couponUseReserved(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<CouponUseReleasedEvent>> couponUseReservationReleasedEvent() {
    return m -> couponEventHandler.couponUseReservationReleased(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<PaymentCompletedEvent>> paymentCompletedEvent() {
    return m -> billingEventHandler.paymentCompleted(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<RefundCompletedEvent>> refundCompletedEvent() {
    return m -> billingEventHandler.refundCompleted(m, m.getPayload());
  }
  
  @Bean
  public Consumer<Message<ProductAuthorizedEvent>> productAuthorizedEvent() {
    return m -> productAuthEventHandler.productAuthorized(m, m.getPayload());
  }
}
