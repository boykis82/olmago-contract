package team.caltech.olmago.contract.domain.factory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.contract.ContractFixtures;
import team.caltech.olmago.contract.domain.contract.ContractType;
import team.caltech.olmago.contract.domain.product.ProductSubscription;
import team.caltech.olmago.contract.domain.product.factory.AllProductsFactory;
import team.caltech.olmago.contract.domain.product.factory.ProductFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UzooPassAllProductFactoryTest extends ProductFactoryTestBase {
  ProductFactory uzooPassAllProductFactory;
  
  static final String uzooPassAllProductCode = "NMP0000001";

  static final String basicBenefitProdCode1 = "NMB0000001";
  static final String basicBenefitProdCode2 = "NMB0000002";

  static final String basicBenefitDcCode = "DCB0000001";
  static final String firstSubDcCode = "DCP0000001";

  static final String baeminProductCode = "NMO0000001";
  static final String yanoljaProductCode = "NMO0000010";

  static final LocalDateTime subRcvDtm = LocalDateTime.of(2022,9,2,12,13,23);

  @Before
  public void setup() {
    // mocking
    mockFindProduct(uzooPassAllProductCode);
    mockFindProduct(basicBenefitProdCode1);
    mockFindProduct(basicBenefitProdCode2);
    mockFindAvailableOptionProducts(uzooPassAllProductCode, baeminProductCode, yanoljaProductCode);
    mockFindAllProduct(baeminProductCode, yanoljaProductCode);
  }

  @After
  public void tearDown() {

  }

  @Test
  public void 이동전화연계없이_배민과_비최초가입이면_상품가입3건_기본혜택할인2건생성된다() {
    // given
    Contract allContract = uzooPassAllContract(subRcvDtm);
    Contract optContract = baeminContract(subRcvDtm, ContractType.OPTION);

    // 비최초가입
    mockNotFirstSubscription(allContract);
    // 이동전화 연계없음
    mockNotLinkedMobilePhone(allContract);
    // 최초가입가능한 옵션
    mockFindOptionContractByPackageContract(allContract, optContract);
    // 기본혜택할인
    mockBasicBenefitDc();
  
    uzooPassAllProductFactory = AllProductsFactory.uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
    
    // when
    List<ProductSubscription> prodSubs = uzooPassAllProductFactory.receiveSubscription(allContract, subRcvDtm);
    allContract.addProductSubscriptions(prodSubs);

    // then
    List<String> allProductCodes = getAllProductCodes(allContract);
    List<String> allDiscountCodes = getAllDiscountCodes(allContract);

    assertThat(uzooPassAllProductFactory.productCode()).isEqualTo(uzooPassAllProductCode);
    // 상품가입 3건
    assertThat(allProductCodes).hasSize(3);
    assertThat(allProductCodes).contains(uzooPassAllProductCode, basicBenefitProdCode1, basicBenefitProdCode2);
    // 할인가입 2건 (기본혜택할인 2건)
    assertThat(allDiscountCodes).hasSize(2);
    assertThat(allDiscountCodes).contains(basicBenefitDcCode, basicBenefitDcCode);
  }
  
  @Test
  public void 이동전화연계없이_배민과_최초가입하면_상품가입3건_최초가입할인1건_기본혜택할인2건생성된다() {
    // given
    Contract allContract = uzooPassAllContract(subRcvDtm);
    Contract optContract = baeminContract(subRcvDtm, ContractType.OPTION);
    
    // 최초가입
    mockFirstSubscription(allContract);
    // 이동전화 연계없음
    mockNotLinkedMobilePhone(allContract);
    // 최초가입가능한 옵션
    mockFindOptionContractByPackageContract(allContract, optContract);
    // 기본혜택할인
    mockBasicBenefitDc();
    // 최초가입할인
    mockFindAllDiscountPolicy(firstSubDcCode);
  
    uzooPassAllProductFactory = AllProductsFactory.uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);

    // when
    List<ProductSubscription> prodSubs = uzooPassAllProductFactory.receiveSubscription(allContract, subRcvDtm);
    allContract.addProductSubscriptions(prodSubs);

    // then
    List<String> allProductCodes = getAllProductCodes(allContract);
    List<String> allDiscountCodes = getAllDiscountCodes(allContract);

    assertThat(uzooPassAllProductFactory.productCode()).isEqualTo(uzooPassAllProductCode);
    // 상품가입 3건
    assertThat(allProductCodes).hasSize(3);
    assertThat(allProductCodes).contains(uzooPassAllProductCode, basicBenefitProdCode1, basicBenefitProdCode2);
    // 할인가입 3건(최초가입할인, 기본혜택할인
    assertThat(allDiscountCodes).hasSize(3);
    assertThat(allDiscountCodes).contains(firstSubDcCode, basicBenefitDcCode, basicBenefitDcCode);
  }

  @Test
  public void 이동전화연계없이_야놀자와_최초가입하면_상품가입3건_최초가입할인0건_기본혜택할인2건생성된다() {
    // given
    Contract allContract = uzooPassAllContract(subRcvDtm);
    Contract optContract = ContractFixtures.createYanoljaContract(subRcvDtm, ContractType.OPTION);

    // 최초가입
    mockFirstSubscription(allContract);
    // 이동전화 연계없음
    mockNotLinkedMobilePhone(allContract);
    // 최초가입불가한 옵션
    mockFindOptionContractByPackageContract(allContract, optContract);
    // 기본혜택할인
    mockBasicBenefitDc();
    // 최초가입할인
    mockFindAllDiscountPolicy(firstSubDcCode);
  
    uzooPassAllProductFactory = AllProductsFactory.uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);

    // when
    List<ProductSubscription> prodSubs = uzooPassAllProductFactory.receiveSubscription(allContract, subRcvDtm);
    allContract.addProductSubscriptions(prodSubs);

    // then
    List<String> allProductCodes = getAllProductCodes(allContract);
    List<String> allDiscountCodes = getAllDiscountCodes(allContract);

    assertThat(uzooPassAllProductFactory.productCode()).isEqualTo(uzooPassAllProductCode);
    // 상품가입 3건
    assertThat(allProductCodes).hasSize(3);
    assertThat(allProductCodes).contains(uzooPassAllProductCode, basicBenefitProdCode1, basicBenefitProdCode2);
    // 할인가입 2건(최초가입할인, 기본혜택할인
    assertThat(allDiscountCodes).hasSize(2);
    assertThat(allDiscountCodes).contains(basicBenefitDcCode, basicBenefitDcCode);
  }

  // 기본혜택할인
  private void mockBasicBenefitDc() {
    mockFindAllDiscountPolicy(basicBenefitDcCode);
  }
  
  private Contract uzooPassAllContract(LocalDateTime subRcvDtm) {
    return Contract.builder()
        .customerId(1L)
        .orderId(2L)
        .contractType(ContractType.PACKAGE)
        .feeProductCode("NMP0000001")
        .subRcvDtm(subRcvDtm)
        .build();
  }
  
  private Contract baeminContract(LocalDateTime subRcvDtm, ContractType contractType) {
    return Contract.builder()
        .customerId(1L)
        .orderId(2L)
        .contractType(contractType)
        .feeProductCode("NMO0000001")
        .subRcvDtm(subRcvDtm)
        .build();
  }
}
