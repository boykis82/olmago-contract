package team.caltech.olmago.contract.product.factory;

import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.plm.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

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
  
  @MockBean
  ProductRelationRepository productRelationRepository;
  
  // 최초가입아님
  protected void mockFirstSubscription(Contract contract) {
    when(contractRepository.countAppliedDcTypeByCustomer(contract.getCustomerId(), DiscountType.THE_FIRST_SUBSCRIPTION))
        .thenReturn(1L);
  }
  
  // 최초가입
  protected void mockNotFirstSubscription(Contract contract) {
    when(contractRepository.countAppliedDcTypeByCustomer(contract.getCustomerId(), DiscountType.THE_FIRST_SUBSCRIPTION))
        .thenReturn(0L);
  }
  
  // 이동전화연계없음
  protected void mockNotLinkedMobilePhone(Contract contract) {
    when(customerServiceProxy.findByCustomerId(contract.getCustomerId()))
        .thenReturn(Mono.empty());
  }
  
  // 패키지계약으로부터 옵션계약 찾기
  protected void mockFindOptionContractByPackageContract(Contract packageContract, Contract optionContract) {
    when(contractRepository.findOptionContractByPackageContract(packageContract))
        .thenReturn(Optional.of(optionContract));
  }

}
