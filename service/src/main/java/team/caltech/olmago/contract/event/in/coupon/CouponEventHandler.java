package team.caltech.olmago.contract.event.in.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.dto.ReceiveCouponDiscountDto;
import team.caltech.olmago.contract.dto.ReleaseCouponDiscountDto;
import team.caltech.olmago.contract.event.in.common.EventInBoxProcessor;
import team.caltech.olmago.contract.service.ContractService;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CouponEventHandler {
  private final EventInBoxProcessor eventInBoxProcessor;
  private final ContractService contractService;
  
  @Transactional
  public void couponUseReserved(Message<CouponUseReservedEvent> msg, CouponUseReservedEvent event) {
    if (eventInBoxProcessor.notExistedEvent(msg)) {
      eventInBoxProcessor.saveInBoxEvent(msg);
      contractService.receiveCouponDiscount(ReceiveCouponDiscountDto.of(event));
    }
  }
  
  @Transactional
  public void couponUseReservationReleased(Message<CouponUseReleasedEvent> msg, CouponUseReleasedEvent event) {
    if (eventInBoxProcessor.notExistedEvent(msg)) {
      eventInBoxProcessor.saveInBoxEvent(msg);
      contractService.releaseCouponDiscount(ReleaseCouponDiscountDto.of(event));
    }
  }
}
