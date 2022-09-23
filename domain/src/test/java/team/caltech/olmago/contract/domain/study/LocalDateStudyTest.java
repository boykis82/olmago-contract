package team.caltech.olmago.contract.domain.study;

import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateStudyTest {
  @Test
  public void givenNonLastDay_whenTestPlusMonth() {
    LocalDate d1 = LocalDate.of(2022,8,30);
    assertThat(LocalDate.of(2022,9,30)).isEqualTo(d1.plusMonths(1));
  }
  
  @Test
  public void givenLastDay_whenPlusMonth() {
    LocalDate d1 = LocalDate.of(2022,8,31);
    assertThat(LocalDate.of(2022,9,30)).isEqualTo(d1.plusMonths(1));
  }
  
  @Test
  public void givenJanuaryLastDay_whenPlusMonth() {
    LocalDate d1 = LocalDate.of(2022,1,31);
    assertThat(LocalDate.of(2022,2,28)).isEqualTo(d1.plusMonths(1));
    assertThat(LocalDate.of(2022,3,31)).isEqualTo(d1.plusMonths(2));
  }
  
  @Test
  public void givenLeapYearFebruaryLastDay_whenPlusMonth() {
    LocalDate d1 = LocalDate.of(2020,2,29);
    assertThat(LocalDate.of(2020,3,29)).isEqualTo(d1.plusMonths(1));
  }
}
