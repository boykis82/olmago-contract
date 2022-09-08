package team.caltech.olmago.contract.plm;

import lombok.Getter;

@Getter
public enum DiscountPeriodType {
  INFINITE(1200),
  ONE_MONTH(1),
  THREE_MONTHS(3),
  SIX_MONTHS(6);
  
  private final long months;
  
  DiscountPeriodType(long months) {
    this.months = months;
  }
}
