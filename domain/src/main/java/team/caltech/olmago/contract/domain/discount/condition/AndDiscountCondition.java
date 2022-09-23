package team.caltech.olmago.contract.domain.discount.condition;

import team.caltech.olmago.contract.domain.contract.Contract;

public class AndDiscountCondition extends DiscountCondition {
  private final DiscountCondition condition1;
  private final DiscountCondition condition2;
  
  public AndDiscountCondition(DiscountCondition condition1, DiscountCondition condition2) {
    this.condition1 = condition1;
    this.condition2 = condition2;
  }
  
  public boolean satisfied(Contract contract) {
    return condition1.satisfied(contract) && condition2.satisfied(contract);
  }
}
