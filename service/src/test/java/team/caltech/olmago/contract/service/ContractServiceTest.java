package team.caltech.olmago.contract.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(ContractTestConfig.class)
public class ContractServiceTest {
  @Test
  public void test1() {
    assertThat(1).isEqualTo(1);
  }
}
