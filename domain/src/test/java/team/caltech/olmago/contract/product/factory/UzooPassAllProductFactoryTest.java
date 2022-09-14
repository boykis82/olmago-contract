package team.caltech.olmago.contract.product.factory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.ContractFixtures;
import team.caltech.olmago.contract.contract.ContractType;
import team.caltech.olmago.contract.customer.LinkedMobilePhoneInfo;
import team.caltech.olmago.contract.plm.DiscountType;
import team.caltech.olmago.contract.product.ProductSubscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static team.caltech.olmago.contract.contract.ContractFixtures.createBaeminContract;
import static team.caltech.olmago.contract.contract.ContractFixtures.createUzoopassAllContract;
import static team.caltech.olmago.contract.product.factory.ProductRelation.ProductRelationType.PACKAGE_AND_OPTION;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UzooPassAllProductFactoryTest extends ProductFactoryTestBase {
  @Autowired
  ProductFactory uzooPassAllProductFactory;
  
  String uzooPassAllProductCode = "NMP0000001";
  
  String basicBenefitProdCode1 = "NMB0000001";
  String basicBenefitProdCode2 = "NMB0000002";
  
  String basicBenefitDcCode = "DCB0000001";
  String firstSubDcCode = "DCP0000001";
  
  LocalDateTime subRcvDtm = LocalDateTime.of(2022,9,2,12,13,23);

  @Before
  public void setup() {
    // mocking
    when(productRepository.findById(uzooPassAllProductCode))
        .thenReturn(Optional.of(products.get(uzooPassAllProductCode)));
    when(productRepository.findById(basicBenefitProdCode1))
        .thenReturn(Optional.of(products.get(basicBenefitProdCode1)));
    when(productRepository.findById(basicBenefitProdCode2))
        .thenReturn(Optional.of(products.get(basicBenefitProdCode2)));
  
    //-- mocking하기 전에 이미 factory bean이 생성되어버린다.. 흠..
    
    when(productRelationRepository.findByMainProductAndProductRelationType(any(), any(), any()))
        .thenReturn(List.of("NMO0000001", "NMO0000010"));   // 각각 최초가입할인 가능한 배민, 최초가입할인 불가능한 야놀자
  
    when(productRepository.findAllById(List.of("NMO0000001", "NMO0000010")))
        .thenReturn(List.of(products.get("NMO0000001"), products.get("NMO0000010")));
  }
  
  @Test
  public void 우주패스all상품팩토리로_이동전화연계없이_비최초가입하면_상품가입3건_기본혜택할인2건생성된다() {
    // given
    Contract allContract = createUzoopassAllContract(subRcvDtm);
    Contract optContract = createBaeminContract(subRcvDtm, ContractType.OPTION);

    // 비최초가입
    mockNotFirstSubscription(allContract);
    // 이동전화 연계없음
    mockNotLinkedMobilePhone(allContract);
    // 최초가입가능한 옵션
    mockFindOptionContractByPackageContract(allContract, optContract);
  
    // 기본혜택할인
    when(discountPolicyRepository.findAllById(List.of(basicBenefitDcCode)))
        .thenReturn(List.of(discountPolicies.get(basicBenefitDcCode)));
    
    // when
    List<ProductSubscription> prodSubs = uzooPassAllProductFactory.receiveSubscription(allContract, subRcvDtm);
    allContract.addProductSubscriptions(prodSubs);
    List<String> allProductCodes = allContract.getAllProductCodes();
    List<String> allDiscountCodes = allContract.getAllDiscountCodes();
  
    // then
    assertThat(uzooPassAllProductFactory.productCode()).isEqualTo(uzooPassAllProductCode);
    // 상품가입 3건
    assertThat(allProductCodes).hasSize(3);
    assertThat(allProductCodes).contains(uzooPassAllProductCode, basicBenefitProdCode1, basicBenefitProdCode2);
    // 할인가입 2건
    assertThat(allDiscountCodes).hasSize(2);
    assertThat(allDiscountCodes).contains(basicBenefitDcCode, basicBenefitDcCode);
  }
  
  @Test
  public void 우주패스all상품팩토리로_이동전화연계없이_최초가입하면_상품가입3건_최초가입할인1건_기본혜택할인2건생성된다() {
    // given
    Contract allContract = createUzoopassAllContract(subRcvDtm);
    Contract optContract = createBaeminContract(subRcvDtm, ContractType.OPTION);
    
    // 최초가입
    mockFirstSubscription(allContract);
    // 이동전화 연계없음
    mockNotLinkedMobilePhone(allContract);
    // 최초가입가능한 옵션
    mockFindOptionContractByPackageContract(allContract, createBaeminContract(subRcvDtm, ContractType.OPTION));
    
    // 최초가입할인
    when(discountPolicyRepository.findAllById(List.of(firstSubDcCode)))
        .thenReturn(List.of(discountPolicies.get(firstSubDcCode)));
    // 기본혜택할인
    when(discountPolicyRepository.findAllById(List.of(basicBenefitDcCode)))
        .thenReturn(List.of(discountPolicies.get(basicBenefitDcCode)));

    // when
    List<ProductSubscription> prodSubs = uzooPassAllProductFactory.receiveSubscription(allContract, subRcvDtm);
    allContract.addProductSubscriptions(prodSubs);
    List<String> allProductCodes = allContract.getAllProductCodes();
    List<String> allDiscountCodes = allContract.getAllDiscountCodes();
    
    // then
    assertThat(uzooPassAllProductFactory.productCode()).isEqualTo(uzooPassAllProductCode);
    // 상품가입 3건
    assertThat(allProductCodes).hasSize(3);
    assertThat(allProductCodes).contains(uzooPassAllProductCode, basicBenefitProdCode1, basicBenefitProdCode2);
    // 할인가입 3건
    assertThat(allDiscountCodes).hasSize(3);
    assertThat(allDiscountCodes).contains(firstSubDcCode, firstSubDcCode, basicBenefitDcCode);
  }
  
}
