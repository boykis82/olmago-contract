package team.caltech.olmago.contract.domain.discount.condition.detail.util;

import team.caltech.olmago.contract.domain.util.TriFunction;

public enum BiCompareOperator {
  GELE((l,v,u) -> l.intValue() <= v.intValue() && v.intValue() <= u.intValue()),
  GTLE((l,v,u) -> l.intValue() < v.intValue() && v.intValue() <= u.intValue()),
  GTLT((l,v,u) -> l.intValue() < v.intValue() && v.intValue() < u.intValue()),
  GELT((l,v,u) -> l.intValue() <= v.intValue() && v.intValue() < u.intValue());
  
  private final TriFunction<Integer, Integer, Integer, Boolean> cmpFunc;
  
  BiCompareOperator(TriFunction<Integer, Integer, Integer, Boolean> cmpFunc) {
    this.cmpFunc = cmpFunc;
  }
  
  public boolean compare(int l, int value, int u) {
    return cmpFunc.apply(l, value, u);
  }
}
