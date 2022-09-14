package team.caltech.olmago.contract.product.factory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.ContractFixtures;
import team.caltech.olmago.contract.contract.ContractType;
import team.caltech.olmago.contract.plm.*;
import team.caltech.olmago.contract.product.ProductSubscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaeminProductFactoryTest extends ProductFactoryTestBase {
  @Autowired
  ProductFactory baeminProductFactory;
  
  String baeminProductCode = "NMO0000001";
  String optDcCode = "DCO0000003";
  String firstSubDcCode = "DCU0000009";
  
  LocalDateTime subRcvDtm = LocalDateTime.of(2022,9,2,12,13,23);

  @Before
  public void setup() {
    // mocking
    when(productRepository.findById(baeminProductCode))
        .thenReturn(Optional.of(products.get(baeminProductCode)));
  
    when(discountPolicyRepository.findAllById(List.of(optDcCode)))
        .thenReturn(List.of(discountPolicies.get(optDcCode)));
    when(discountPolicyRepository.findAllById(List.of(firstSubDcCode)))
        .thenReturn(List.of(discountPolicies.get(firstSubDcCode)));
    when(discountPolicyRepository.findAllById(List.of(optDcCode, firstSubDcCode)))
        .thenReturn(List.of(discountPolicies.get(optDcCode), discountPolicies.get(firstSubDcCode)));
  }
  
  @After
  public void tearDown() {
  
  }
  
  @Test
  public void 배민상품팩토리로_배민옵션가입하면_상품가입1건옵션할인1건생성된다() {
    // given
    Contract baeminOptContract = ContractFixtures.createBaeminContract(subRcvDtm, ContractType.OPTION);
  
    when(contractRepository.countAppliedDcTypeByCustomer(baeminOptContract.getCustomerId(), DiscountType.THE_FIRST_SUBSCRIPTION))
        .thenReturn(0L);
    
    // when
    List<ProductSubscription> prodSubs = baeminProductFactory.receiveSubscription(baeminOptContract, subRcvDtm);
  
    // then
    assertThat(baeminProductFactory.productCode()).isEqualTo(baeminProductCode);
    // 상품가입 1건
    assertThat(prodSubs).hasSize(1);
    assertThat(prodSubs.get(0).getProductCode()).isEqualTo(baeminProductCode);
    // 할인가입 1건
    assertThat(prodSubs.stream().map(ProductSubscription::getDiscountSubscriptions).mapToLong(List::size).sum()).isEqualTo(1);
    assertThat(prodSubs.get(0).getDiscountSubscriptions().get(0).getDiscountPolicy().getDcPolicyCode()).isEqualTo(optDcCode);
  }
  
  @Test
  public void 배민상품팩토리로_배민단품최초가입하면_상품가입1건최초가입할인1건생성된다() {
    // given
    Contract baeminUnitContract = ContractFixtures.createBaeminContract(subRcvDtm, ContractType.UNIT);
  
    when(contractRepository.countAppliedDcTypeByCustomer(baeminUnitContract.getCustomerId(), DiscountType.THE_FIRST_SUBSCRIPTION))
        .thenReturn(0L);
  
    // when
    List<ProductSubscription> prodSubs = baeminProductFactory.receiveSubscription(baeminUnitContract, subRcvDtm);
  
    // then
    assertThat(baeminProductFactory.productCode()).isEqualTo(baeminProductCode);
    // 상품가입 1건
    assertThat(prodSubs).hasSize(1);
    assertThat(prodSubs.get(0).getProductCode()).isEqualTo(baeminProductCode);
    // 할인가입 1건
    assertThat(prodSubs.stream().map(ProductSubscription::getDiscountSubscriptions).mapToLong(List::size).sum()).isEqualTo(1);
    assertThat(prodSubs.get(0).getDiscountSubscriptions().get(0).getDiscountPolicy().getDcPolicyCode()).isEqualTo(firstSubDcCode);
  }
  
  @Test
  public void 배민상품팩토리로_배민단품비최초가입하면_상품가입1건생성_할인미생성된다() {
    // given
    Contract baeminUnitContract = ContractFixtures.createBaeminContract(subRcvDtm, ContractType.UNIT);
  
    when(contractRepository.countAppliedDcTypeByCustomer(baeminUnitContract.getCustomerId(), DiscountType.THE_FIRST_SUBSCRIPTION))
        .thenReturn(1L);
  
    // when
    List<ProductSubscription> prodSubs = baeminProductFactory.receiveSubscription(baeminUnitContract, subRcvDtm);
  
    // then
    assertThat(baeminProductFactory.productCode()).isEqualTo(baeminProductCode);
    // 상품가입 1건
    assertThat(prodSubs).hasSize(1);
    assertThat(prodSubs.get(0).getProductCode()).isEqualTo(baeminProductCode);
    // 할인가입 0건
    assertThat(prodSubs.stream().map(ProductSubscription::getDiscountSubscriptions).mapToLong(List::size).sum()).isEqualTo(0);
  }
}
