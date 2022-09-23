package team.caltech.olmago.contract.domain.discount.condition;

import team.caltech.olmago.contract.domain.contract.Contract;

public class NegatedDiscountCondition extends DiscountCondition {
  DiscountCondition condition;
  
  public NegatedDiscountCondition(DiscountCondition condition) {
    this.condition = condition;
  }
  
  public boolean satisfied(Contract contract) {
    return !condition.satisfied(contract);
  }
}
