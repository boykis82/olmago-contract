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
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.ContractFixtures;
import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.contract.ContractType;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.plm.*;
import team.caltech.olmago.contract.product.ProductSubscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductFactoryTest {
  private static final Map<String, Product> products;
  private static final Map<String, DiscountPolicy> discountPolicies;
  
  static {
    products = PlmFixtures.setupProducts().stream()
        .collect(Collectors.toMap(Product::getProductCode, p -> p));
    discountPolicies = PlmFixtures.setupDiscountPolicies().stream()
        .collect(Collectors.toMap(DiscountPolicy::getDcPolicyCode, d -> d));
  }
  
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
  
  @Test
  public void 배민상품팩토리로_배민옵션가입하면_상품가입1건옵션할인1건생성된다() {
    // given
    String baeminProductCode = "NMO0000001";
    LocalDateTime subRcvDtm = LocalDateTime.of(2022,9,2,12,13,23);
    Contract baeminOptContract = ContractFixtures.createBaeminContract(subRcvDtm, ContractType.OPTION);
    
    when(productRepository.findById(baeminProductCode))
        .thenReturn(Optional.of(products.get(baeminProductCode)));
  
    when(discountPolicyRepository.findAllById(any()))
        .thenReturn(List.of(discountPolicies.get("DCO0000003")));
    
    // when
    List<ProductSubscription> prodSubs = baeminProductFactory.receiveSubscription(baeminOptContract, subRcvDtm);
  
    // then
    assertThat(baeminProductFactory.productCode()).isEqualTo(baeminProductCode);
    // 상품가입 1건
    assertThat(prodSubs).hasSize(1);
    // 할인가입 1건
    assertThat(prodSubs.stream().map(ProductSubscription::getDiscountSubscriptions).mapToLong(List::size).sum()).isEqualTo(1);
  }
}
