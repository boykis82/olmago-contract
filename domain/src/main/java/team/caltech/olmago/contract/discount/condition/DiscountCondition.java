package team.caltech.olmago.contract.discount.condition;

import team.caltech.olmago.contract.contract.Contract;

import java.util.Arrays;
import java.util.List;

public abstract class DiscountCondition {
  List<String> discountPolicyIds;
  
  public abstract boolean satisfied(Contract contract);
  
  public static DiscountCondition and(DiscountCondition dc1, DiscountCondition dc2) {
    return new AndDiscountCondition(dc1, dc2);
  }
  
  public static DiscountCondition and(DiscountCondition dc1, DiscountCondition dc2, DiscountCondition dc3) {
    return new AndDiscountCondition(and(dc1, dc2), dc3);
  }
  
  public static DiscountCondition and(DiscountCondition dc1, DiscountCondition dc2, DiscountCondition dc3, DiscountCondition dc4) {
    return new AndDiscountCondition(and(dc1, dc2, dc3), dc4);
  }
  
  public static DiscountCondition or(DiscountCondition dc1, DiscountCondition dc2) {
    return new OrDiscountCondition(dc1, dc2);
  }
  
  public static DiscountCondition neg(DiscountCondition dc) {
    return new NegatedDiscountCondition(dc);
  }
  
  public List<String> discountPolicyIds() {
    return discountPolicyIds;
  }

  public DiscountCondition discountPolicyIds(String ...discountPolicyIds) {
    this.discountPolicyIds = Arrays.asList(discountPolicyIds);
    return this;
  }
}
