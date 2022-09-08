package team.caltech.olmago.contract.discount.condition.detail;

import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.discount.condition.DiscountCondition;

public class AlwaysTrueDcCond extends DiscountCondition {
  @Override
  public boolean satisfied(Contract contract) {
    return true;
  }
}
