package team.caltech.olmago.contract;

import org.junit.Test;
import team.caltech.olmago.contract.contract.BillCycle;
import team.caltech.olmago.contract.plm.BillPeriod;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class BillCycleTest {
  
  @Test
  public void givenLastDayBillCycle_whenPlusMonth() {
    BillCycle theFirstBillCycle = BillCycle.of(LocalDate.of(2022,1,31), BillPeriod.MONTHLY);
    assertThat(theFirstBillCycle.getCurrentBillStartDate()).isEqualTo(LocalDate.of(2022,1,31));
    assertThat(theFirstBillCycle.getCurrentBillEndDate()).isEqualTo(LocalDate.of(2022,2,27));
    
    BillCycle nextBillCycle = theFirstBillCycle.next();
    assertThat(nextBillCycle.getCurrentBillStartDate()).isEqualTo(LocalDate.of(2022,2,28));
    assertThat(nextBillCycle.getCurrentBillEndDate()).isEqualTo(LocalDate.of(2022,3,30));
  }
  
  @Test
  public void givenNormalDayBillCycle_whenPlusMonth() {
    BillCycle theFirstBillCycle = BillCycle.of(LocalDate.of(2022,1,28), BillPeriod.MONTHLY);
    assertThat(theFirstBillCycle.getCurrentBillStartDate()).isEqualTo(LocalDate.of(2022,1,28));
    assertThat(theFirstBillCycle.getCurrentBillEndDate()).isEqualTo(LocalDate.of(2022,2,27));
  
    BillCycle nextBillCycle = theFirstBillCycle.next();
    assertThat(nextBillCycle.getCurrentBillStartDate()).isEqualTo(LocalDate.of(2022,2,28));
    assertThat(nextBillCycle.getCurrentBillEndDate()).isEqualTo(LocalDate.of(2022,3,27));
  }
}
