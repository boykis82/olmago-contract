package team.caltech.olmago.contract.plm;

import lombok.Getter;

@Getter
public enum BillPeriod {
  YEARLY(12),
  MONTHLY(1);
  
  private final int months;
  
  BillPeriod(int months) {
    this.months = months;
  }
}
