package team.caltech.olmago.contract.domain.factory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.contract.ContractFixtures;
import team.caltech.olmago.contract.domain.contract.ContractType;
import team.caltech.olmago.contract.domain.product.ProductSubscription;
import team.caltech.olmago.contract.domain.product.factory.ProductFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaeminProductFactoryTest extends ProductFactoryTestBase {
  @Autowired
  @Lazy
  ProductFactory baeminProductFactory;

  static final String baeminProductCode = "NMO0000001";
  static final String optDcCode = "DCO0000003";
  static final String firstSubDcCode = "DCU0000009";

  static final LocalDateTime subRcvDtm = LocalDateTime.of(2022,9,2,12,13,23);

  @Before
  public void setup() {
    // mocking
    mockFindProduct(baeminProductCode);
    mockFindAllDiscountPolicy(optDcCode);
    mockFindAllDiscountPolicy(firstSubDcCode);
    mockFindAllDiscountPolicy(optDcCode, firstSubDcCode);
  }
  
  @After
  public void tearDown() {
  
  }
  
  @Test
  public void 배민상품팩토리로_배민옵션가입하면_상품가입1건옵션할인1건생성된다() {
    // given
    Contract baeminOptContract = ContractFixtures.createBaeminContract(subRcvDtm, ContractType.OPTION);
  
    // 최초가입
    mockFirstSubscription(baeminOptContract);
    
    // when
    List<ProductSubscription> prodSubs = baeminProductFactory.receiveSubscription(baeminOptContract, subRcvDtm);
    baeminOptContract.addProductSubscriptions(prodSubs);

    // then
    List<String> allProductCodes = getAllProductCodes(baeminOptContract);
    List<String> allDiscountCodes = getAllDiscountCodes(baeminOptContract);

    assertThat(baeminProductFactory.productCode()).isEqualTo(baeminProductCode);
    // 상품가입 1건
    assertThat(allProductCodes).hasSize(1);
    assertThat(allProductCodes).contains(baeminProductCode);
    // 할인가입 1건
    assertThat(allDiscountCodes).hasSize(1);
    assertThat(allDiscountCodes).contains(optDcCode);
  }
  
  @Test
  public void 배민상품팩토리로_배민단품최초가입하면_상품가입1건최초가입할인1건생성된다() {
    // given
    Contract baeminUnitContract = ContractFixtures.createBaeminContract(subRcvDtm, ContractType.UNIT);
  
    // 최초가입
    mockFirstSubscription(baeminUnitContract);

    // when
    List<ProductSubscription> prodSubs = baeminProductFactory.receiveSubscription(baeminUnitContract, subRcvDtm);
    baeminUnitContract.addProductSubscriptions(prodSubs);

    // then
    List<String> allProductCodes = getAllProductCodes(baeminUnitContract);
    List<String> allDiscountCodes = getAllDiscountCodes(baeminUnitContract);

    assertThat(baeminProductFactory.productCode()).isEqualTo(baeminProductCode);
    // 상품가입 1건
    assertThat(allProductCodes).hasSize(1);
    assertThat(allProductCodes).contains(baeminProductCode);
    // 할인가입 1건
    assertThat(allDiscountCodes).hasSize(1);
    assertThat(allDiscountCodes).contains(firstSubDcCode);
  }
  
  @Test
  public void 배민상품팩토리로_배민단품비최초가입하면_상품가입1건생성_할인미생성된다() {
    // given
    Contract baeminUnitContract = ContractFixtures.createBaeminContract(subRcvDtm, ContractType.UNIT);
  
    // 최초가입아님
    mockNotFirstSubscription(baeminUnitContract);
  
    // when
    List<ProductSubscription> prodSubs = baeminProductFactory.receiveSubscription(baeminUnitContract, subRcvDtm);
    baeminUnitContract.addProductSubscriptions(prodSubs);

    // then
    List<String> allProductCodes = getAllProductCodes(baeminUnitContract);
    List<String> allDiscountCodes = getAllDiscountCodes(baeminUnitContract);

    assertThat(baeminProductFactory.productCode()).isEqualTo(baeminProductCode);
    // 상품가입 1건
    assertThat(allProductCodes).hasSize(1);
    assertThat(allProductCodes).contains(baeminProductCode);
    // 할인가입 0건
    assertThat(allDiscountCodes).isEmpty();
  }
}
