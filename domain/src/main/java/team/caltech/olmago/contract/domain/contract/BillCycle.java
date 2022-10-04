package team.caltech.olmago.contract.domain.contract;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.domain.plm.product.BillPeriod;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Embeddable
public class BillCycle {
  @Column(name = "fst_bill_sta_dt")
  private LocalDate theFirstBillStartDate;
  @Column(name = "month_passed")
  private Integer monthsPassed;
  @Column(name = "cur_bill_sta_dt")
  private LocalDate currentBillStartDate;
  @Column(name = "cur_bill_end_dt")
  private LocalDate currentBillEndDate;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "bill_period")
  private BillPeriod billPeriod;
  
  @Builder
  private BillCycle(LocalDate theFirstBillStartDate, BillPeriod billPeriod, int monthsPassed) {
    this.theFirstBillStartDate = theFirstBillStartDate;
    this.monthsPassed = monthsPassed;
    this.billPeriod = billPeriod;
    
    currentBillStartDate = theFirstBillStartDate.plusMonths(monthsPassed);
    currentBillEndDate = theFirstBillStartDate.plusMonths(monthsPassed + 1).minusDays(1);
  }
  
  public static BillCycle of(LocalDate theFirstBillStartDate, BillPeriod billPeriod) {
    return BillCycle.builder()
        .theFirstBillStartDate(theFirstBillStartDate)
        .billPeriod(billPeriod)
        .monthsPassed(0)
        .build();
  }
  
  public BillCycle next() {
    return BillCycle.builder()
        .theFirstBillStartDate(theFirstBillStartDate)
        .billPeriod(billPeriod)
        .monthsPassed(monthsPassed + billPeriod.getMonths())
        .build();
  }
  
  public BillCycle prev() {
    return BillCycle.builder()
        .theFirstBillStartDate(theFirstBillStartDate)
        .billPeriod(billPeriod)
        .monthsPassed(monthsPassed - billPeriod.getMonths())
        .build();
  }
}
