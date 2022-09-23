package team.caltech.olmago.contract.domain.discount.condition.detail;

import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.discount.condition.DiscountCondition;

public class AlwaysFalseDcCond extends DiscountCondition {
  @Override
  public boolean satisfied(Contract contract) {
    return false;
  }
}
