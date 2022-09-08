package team.caltech.olmago.contract.discount.condition;

import lombok.Builder;
import team.caltech.olmago.contract.contract.Contract;

public class OrDiscountCondition extends DiscountCondition {
  private final DiscountCondition condition1;
  private final DiscountCondition condition2;
  
  @Builder
  public OrDiscountCondition(DiscountCondition condition1, DiscountCondition condition2) {
    this.condition1 = condition1;
    this.condition2 = condition2;
  }
  
  public boolean satisfied(Contract contract) {
    return condition1.satisfied(contract) || condition2.satisfied(contract);
  }
}
