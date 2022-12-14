package team.caltech.olmago.contract.domain.factory;

import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.plm.PlmFixtures;
import team.caltech.olmago.contract.domain.product.factory.ProductRelationRepository;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.plm.discount.DiscountType;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;
import team.caltech.olmago.contract.domain.product.ProductSubscription;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static team.caltech.olmago.contract.domain.product.factory.ProductRelation.ProductRelationType.PACKAGE_AND_OPTION;

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
  
  // ????????????
  protected void mockFirstSubscription(Contract contract) {
    when(contractRepository.countAppliedDcTypeByCustomer(contract.getCustomerId(), DiscountType.THE_FIRST_SUBSCRIPTION))
        .thenReturn(0L);
  }
  
  // ??????????????????
  protected void mockNotFirstSubscription(Contract contract) {
    when(contractRepository.countAppliedDcTypeByCustomer(contract.getCustomerId(), DiscountType.THE_FIRST_SUBSCRIPTION))
        .thenReturn(2L);
  }
  
  // ????????????????????????
  protected void mockNotLinkedMobilePhone(Contract contract) {
    when(customerServiceProxy.findByCustomerId(contract.getCustomerId()))
        .thenReturn(Mono.empty());
  }
  
  // ??????????????????????????? ???????????? ??????
  protected void mockFindOptionContractByPackageContract(Contract packageContract, Contract optionContract) {
    when(contractRepository.findOptionContractByPackageContract(packageContract))
        .thenReturn(Optional.of(optionContract));
  }

  protected void mockFindProduct(String productCode) {
    when(productRepository.findById(productCode))
        .thenReturn(Optional.of(products.get(productCode)));
  }

  protected void mockFindAllProduct(String ... productCodes) {
    List<String> productCodeList = Arrays.asList(productCodes);
    when(productRepository.findAllById(productCodeList))
        .thenReturn(productCodeList.stream().map(products::get).collect(Collectors.toList()));
  }

  protected void mockFindAllDiscountPolicy(String ... dcCodes) {
    List<String> dcCodeList = Arrays.asList(dcCodes);
    when(discountPolicyRepository.findAllById(dcCodeList))
        .thenReturn(dcCodeList.stream().map(discountPolicies::get).collect(Collectors.toList()));
  }

  protected void mockFindAvailableOptionProducts(String pkgProductCode, String ... optionProductCodes) {
    when(productRelationRepository.findByMainProductAndProductRelationType(eq(pkgProductCode), eq(PACKAGE_AND_OPTION), any()))
        .thenReturn(Arrays.asList(optionProductCodes));   // ?????? ?????????????????? ????????? ??????, ?????????????????? ???????????? ?????????
  }


  protected List<String> getAllProductCodes(Contract contract) {
    return contract.getProductSubscriptions().stream()
        .map(ProductSubscription::getProductCode)
        .collect(Collectors.toList());
  }

  protected List<String> getAllDiscountCodes(Contract contract) {
    return contract.getProductSubscriptions().stream()
        .map(ProductSubscription::getDiscountSubscriptions)
        .flatMap(List::stream)
        .map(ds -> ds.getDiscountPolicy().getDcPolicyCode())
        .collect(Collectors.toList());
  }
}
