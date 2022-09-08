package team.caltech.olmago.contract.discount.condition;

import team.caltech.olmago.contract.contract.Contract;

public class NegatedDiscountCondition extends DiscountCondition {
  DiscountCondition condition;
  
  public NegatedDiscountCondition(DiscountCondition condition) {
    this.condition = condition;
  }
  
  public boolean satisfied(Contract contract) {
    return !condition.satisfied(contract);
  }
}
