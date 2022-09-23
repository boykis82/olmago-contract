package team.caltech.olmago.contract.domain.discount.condition.detail.util;

import java.util.function.BiFunction;

public enum CompareOperator {
  EQ((a,b) -> a.intValue() == b.intValue()),
  GT((a,b) -> a.intValue() > b.intValue()),
  LT((a,b) -> a.intValue() < b.intValue()),
  GE((a,b) -> a.intValue() >= b.intValue()),
  LE((a,b) -> a.intValue() <= b.intValue());
  
  private final BiFunction<Integer, Integer, Boolean> cmpFunc;
  
  CompareOperator(BiFunction<Integer, Integer, Boolean> cmpFunc) {
    this.cmpFunc = cmpFunc;
  }
  
  public boolean compare(int a, int b) {
    return cmpFunc.apply(a, b);
  }
}
