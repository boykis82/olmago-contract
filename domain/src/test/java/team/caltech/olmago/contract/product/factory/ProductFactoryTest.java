package team.caltech.olmago.contract.product.factory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.TestConfig;
import team.caltech.olmago.contract.config.ProductFactoryConfiguration;
import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.plm.DiscountPolicyRepository;
import team.caltech.olmago.contract.plm.ProductRepository;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductFactoryTest {
  @MockBean
  ContractRepository contractRepository;

  @MockBean
  CustomerServiceProxy customerServiceProxy;

  @MockBean
  ProductRepository productRepository;

  @MockBean
  ProductRelationRepository productRelationRepository;

  @MockBean
  DiscountPolicyRepository discountPolicyRepository;

  @Autowired
  ProductFactory baeminProductFactory;
  
  @Before
  public void setup() {
  
  }
  
  @After
  
  
  @Test
  public void 배민상품팩토리를생성하면_적용가능한할인목록을가져온다() {
    assertThat(baeminProductFactory.productCode()).isEqualTo("NMO0000001");
  }
}
