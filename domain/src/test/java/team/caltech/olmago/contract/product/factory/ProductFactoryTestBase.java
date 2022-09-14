package team.caltech.olmago.contract.product.factory;

import org.springframework.boot.test.mock.mockito.MockBean;
import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.plm.*;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class ProductFactoryTestBase {
  protected static final Map<String, Product> products;
  protected static final Map<String, DiscountPolicy> discountPolicies;
  
  static {
    products = PlmFixtures.setupProducts().stream()
        .collect(Collectors.toMap(Product::getProductCode, p -> p));
    discountPolicies = PlmFixtures.setupDiscountPolicies().stream()
        .collect(Collectors.toMap(DiscountPolicy::getDcPolicyCode, d -> d));
  }
  
  @MockBean
  ContractRepository contractRepository;
  
  @MockBean
  ProductRepository productRepository;
  
  @MockBean
  CustomerServiceProxy customerServiceProxy;
  
  @MockBean
  DiscountPolicyRepository discountPolicyRepository;
}
