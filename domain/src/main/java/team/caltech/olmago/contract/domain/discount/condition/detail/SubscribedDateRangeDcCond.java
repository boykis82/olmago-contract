package team.caltech.olmago.contract.domain.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.discount.condition.DiscountCondition;

import java.time.LocalDate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscribedDateRangeDcCond extends DiscountCondition {
  
  private final LocalDate staDt;
  private final LocalDate endDt;
  
  public static SubscribedDateRangeDcCond between(LocalDate staDt, LocalDate endDt) {
    return new SubscribedDateRangeDcCond(staDt, endDt);
  }
  
  @Override
  public boolean satisfied(Contract contract) {
    LocalDate strdDt = contract.getLifeCycle().getSubscriptionReceivedDateTime().toLocalDate();
    return (strdDt.isAfter(staDt) || strdDt.isEqual(staDt)) && (strdDt.isBefore(endDt) || strdDt.isEqual(endDt));
  }
}
