package team.caltech.olmago.contract.domain.plm.product;

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
