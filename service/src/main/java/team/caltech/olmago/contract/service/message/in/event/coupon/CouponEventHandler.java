package team.caltech.olmago.contract.service.message.in.event.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.service.dto.ReceiveCouponDiscountDto;
import team.caltech.olmago.contract.service.dto.ReleaseCouponDiscountDto;
import team.caltech.olmago.contract.service.message.in.common.MessageInBoxProcessor;
import team.caltech.olmago.contract.service.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CouponEventHandler {
  private final MessageInBoxProcessor messageInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void couponUseReserved(Message<?> msg, CouponUseReservedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.receiveCouponDiscount(ReceiveCouponDiscountDto.of(event));
    }
  }
  
  @Transactional
  public void couponUseReservationReleased(Message<?> msg, CouponUseReleasedEvent event) {
    if (messageInBoxProcessor.notExistedMessage(msg)) {
      messageInBoxProcessor.saveInBoxMessage(msg);
      contractService.releaseCouponDiscount(ReleaseCouponDiscountDto.of(event));
    }
  }
}
