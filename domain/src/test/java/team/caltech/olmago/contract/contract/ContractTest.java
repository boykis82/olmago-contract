package team.caltech.olmago.contract.contract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.TestConfig;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(TestConfig.class)
public class ContractTest {
  @Autowired
  ContractRepository contractRepository;
  
  @Before
  public void setUp() {
  
  }
  
  @After
  public void tearDown() {
  
  }
  
  @Test
  public void givenPackage_whenCreateContract_thenShouldBeGetId() {
    Contract contract = Contract.builder()
        .customerId(1L)
        .lastOrderId(2L)
        .contractType(ContractType.PACKAGE)
        .feeProductCode("NM00000001")
        .subRcvDtm(LocalDateTime.of(2022,9,1,10,23,12))
        .build();
    assertThat(contractRepository.save(contract).getId()).isNotZero();
  }
}
