package team.caltech.olmago.contract.contract;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.plm.BillPeriod;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Embeddable
public class BillCycle {
  private LocalDate theFirstBillStartDate;
  private Integer monthsPassed;
  private LocalDate currentBillStartDate;
  private LocalDate currentBillEndDate;
  
  @Enumerated(EnumType.STRING)
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
}
