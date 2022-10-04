package team.caltech.olmago.contract.domain.contract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.plm.PlmFixtures;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoryTest {
  @Autowired
  ContractRepository contractRepository;
  
  @Autowired
  ProductRepository productRepository;
  
  @Autowired
  DiscountPolicyRepository discountPolicyRepository;
  
  // 구현체가 domain안에 없고 service안에 있다보니 bean을 찾지 못해 오류 발생.. 흠. 어떻게 하지
  @MockBean
  CustomerServiceProxy customerServiceProxy;
  
  @Before
  public void setUp() {
    productRepository.saveAll(PlmFixtures.setupProducts());
    discountPolicyRepository.saveAll(PlmFixtures.setupDiscountPolicies());
  }
  
  @After
  public void tearDown() {
    contractRepository.deleteAll();
    productRepository.deleteAll();
    discountPolicyRepository.deleteAll();
  }
  
  @Test
  public void 살아있는서비스2개있을때_고객ID와해지서비스포함으로조회하면_2개가반환된다() {
    LocalDateTime subRcvDtm = LocalDateTime.now();
    Contract c1 = ContractFixtures.createUzoopassAllContract(subRcvDtm);
    Contract c2 = ContractFixtures.createBaeminContract(subRcvDtm, ContractType.OPTION);
  
    contractRepository.save(c1);
    contractRepository.save(c2);
  
    assertThat(contractRepository.findAll()).hasSize(2);
    assertThat(contractRepository.findByCustomerId(2L, true)).hasSize(2);
  }
}
