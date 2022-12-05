package team.caltech.olmago.contract.domain.contract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import team.caltech.olmago.contract.domain.plm.PlmFixtures;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.product.ProductCalculationResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculationTest {
  private static final Map<String, Product> products;
  private static final Map<String, DiscountPolicy> discountPolicies;
  
  static {
    products = PlmFixtures.setupProducts().stream()
        .collect(Collectors.toMap(Product::getProductCode, p -> p));
    discountPolicies = PlmFixtures.setupDiscountPolicies().stream()
        .collect(Collectors.toMap(DiscountPolicy::getDcPolicyCode, d -> d));
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  
  }
  
  @Test
  public void 계약해지접수됐으면_계산안된다() {
    // given
    Contract contract = ContractFixtures.createBaeminContract(1L, 1L, LocalDateTime.now(), ContractType.UNIT, true);
    contract.receiveSubscription();
    contract.completeSubscription(LocalDateTime.now());
    contract.receiveTermination(1L, LocalDateTime.now());
  
    // when
    var calculationResult = contract.calculate(LocalDate.now());
  
    // then
    assertThat(calculationResult).isEmpty();
  }
  
  @Test
  public void 상품변경하면_이전상품은계산안되고_이후상품만계산된다() {
    // given
    Contract contract = ContractFixtures.createUzoopassAllContract(1L, 1L, LocalDateTime.now(), true);
    contract.receiveSubscription();
    contract.completeSubscription(LocalDateTime.now().minusDays(30));
  
    contract.changeContract(
        2L,
        "NMP0000002",
        List.of("NMP0000001", "NMB0000001", "NMB0000002"),
        List.of(
            ContractFixtures.createProductSubscription(contract, "NMP0000002"),
            ContractFixtures.createProductSubscription(contract, "NMB0000003"),
            ContractFixtures.createProductSubscription(contract, "NMB0000004")
        ),
        LocalDateTime.now().minusDays(15)
    );
    
    // when
    var calculationResult = contract.calculate(LocalDate.now()).get();
    
    // then
    assertThat(calculationResult.getContractType()).isEqualTo(ContractType.PACKAGE.name());
    assertThat(calculationResult.getProductCalculationResult("NMP0000001")).isNull();
    assertThat(calculationResult.getProductCalculationResult("NMB0000003")).isNull();
    assertThat(calculationResult.getProductCalculationResult("NMB0000004")).isNull();
    checkProductCalculationResult(calculationResult, "NMP0000002", 9900);
  }
  
  @Test
  public void 배민단품가입접수하면_월정액5000원계산된다() {
    // given
    Contract contract = ContractFixtures.createBaeminContract(1L, 1L, LocalDateTime.now(), ContractType.UNIT, true);
    contract.receiveSubscription();
    
    // when
    var calculationResult = contract.calculate(LocalDate.now()).get();
    
    // then
    assertThat(calculationResult.getContractType()).isEqualTo(ContractType.UNIT.name());
    var productCalculationResult = checkProductCalculationResult(calculationResult, "NMO0000001", 5000);
    assertThat(productCalculationResult.getDiscountCalculationResults()).isEmpty();
  }
  
  @Test
  public void 배민옵션접수하면_월정액5000원과_옵션할인마이너스5000원계산된다() {
    // given
    Contract contract = ContractFixtures.createBaeminContract(1L, 1L, LocalDateTime.now(), ContractType.OPTION, true);
    contract.receiveSubscription();
    
    // when
    var calculationResult = contract.calculate(LocalDate.now()).get();
    
    // then
    assertThat(calculationResult.getContractType()).isEqualTo(ContractType.OPTION.name());
    var productCalculationResult = checkProductCalculationResult(calculationResult, "NMO0000001", 5000);
    checkDiscountCalculationResult(productCalculationResult, "DCO0000003", -5000);
  }
  
  @Test
  public void 우주패스all가입접수하면_월정액9900원계산되고_기본혜택은2000원씩계산되고100퍼할인된다() {
    // given
    Contract contract = ContractFixtures.createUzoopassAllContract(
        1L, 1L, LocalDateTime.now(), true, "DCM0000001", "DCP0000001");
    contract.receiveSubscription();
    
    // when
    var calculationResult = contract.calculate(LocalDate.now()).get();
    
    // then
    assertThat(calculationResult.getContractType()).isEqualTo(ContractType.PACKAGE.name());
    assertThat(calculationResult.getProductCalculationResults()).hasSize(3);
  
    // 우주패스all
    var productCalculationResult = checkProductCalculationResult(calculationResult, "NMP0000001", 9900);
    var discountCalculationResults = productCalculationResult.getDiscountCalculationResults();
    assertThat(discountCalculationResults).hasSize(2);
    checkDiscountCalculationResult(productCalculationResult, "DCP0000001", -8900);
    checkDiscountCalculationResult(productCalculationResult, "DCM0000001", -1000);
  
    // 기본혜택1
    productCalculationResult = checkProductCalculationResult(calculationResult, "NMB0000001", 2000);
    checkDiscountCalculationResult(productCalculationResult, "DCB0000001", -2000);
  
    // 기본혜택2
    productCalculationResult = checkProductCalculationResult(calculationResult, "NMB0000002", 2000);
    checkDiscountCalculationResult(productCalculationResult, "DCB0000001", -2000);
  }
  
  @Test
  public void 최초가입할인은첫달만적용되고_기본혜택할인은계속적용된다() {
    // given
    Contract contract = ContractFixtures.createUzoopassAllContract(
        1L, 1L, LocalDateTime.now(), true, "DCP0000001");
    contract.receiveSubscription();
    contract.completeSubscription(LocalDateTime.now());
    
    // when
    var calculationResult = contract.calculate(LocalDate.now().plusMonths(1)).get();
  
    // then
    var productCalculationResult = checkProductCalculationResult(calculationResult, "NMP0000001", 9900);
    var discountCalculationResults = productCalculationResult.getDiscountCalculationResults();
    assertThat(discountCalculationResults).isEmpty();
  
    // 기본혜택1
    productCalculationResult = checkProductCalculationResult(calculationResult, "NMB0000001", 2000);
    checkDiscountCalculationResult(productCalculationResult, "DCB0000001", -2000);
  
    // 기본혜택2
    productCalculationResult = checkProductCalculationResult(calculationResult, "NMB0000002", 2000);
    checkDiscountCalculationResult(productCalculationResult, "DCB0000001", -2000);
  }
  
  @Test
  public void 이동전화연계할인은_두번째달부터는일할계산된다_할인유지중() {
    // given
    Contract contract = ContractFixtures.createUzoopassAllContract(
        1L, 1L, LocalDateTime.now(), true, "DCM0000001");
    contract.receiveSubscription();
    contract.completeSubscription(LocalDateTime.now());
    
    // when
    var calculationResult = contract.calculate(LocalDate.now().plusMonths(1)).get();
  
    // then
    var productCalculationResult = checkProductCalculationResult(calculationResult, "NMP0000001", 9900);
    assertThat(productCalculationResult.getDiscountCalculationResults()).hasSize(1);
  
    checkDiscountCalculationResult(productCalculationResult, "DCM0000001", -9900);
  }
  
  @Test
  public void 이동전화연계할인은_두번째달부터는일할계산된다_할인이중간에해지() {
    // given - 가입 시에는 연계할인 존재. 10일후에 할인 빠진 상황
    Contract contract = ContractFixtures.createUzoopassAllContract(
        1L, 1L, LocalDateTime.now(), true, "DCM0000001");
    contract.receiveSubscription();
    contract.completeSubscription(LocalDateTime.now());
    contract.changeMobilePhoneLinkedDiscount(Collections.emptyList(), LocalDateTime.now().plusDays(10));
    
    // when
    var calculationResult = contract.calculate(LocalDate.now().plusMonths(1)).get();
    
    // then
    var productCalculationResult = checkProductCalculationResult(calculationResult, "NMP0000001", 9900);
    assertThat(productCalculationResult.getDiscountCalculationResults()).hasSize(1);
    
    long expected = Math.round(9900 * 9.0 / DiscountPolicy.DIVIDE_BY_USE_DAYS * -1);
    checkDiscountCalculationResult(productCalculationResult, "DCM0000001", expected);
  }
  
  @Test
  public void 이동전화연계할인은_두번째달부터는일할계산된다_할인이중간에해지됐다가다른연계할인들어옴() {
    // given - 가입 시에는 연계할인(100%) 존재. 10일후에 할인 빠진 상황. 20일후에 5000원짜리 연계할인 들어옴
    Contract contract = ContractFixtures.createUzoopassAllContract(
        1L, 1L, LocalDateTime.now(), true, "DCM0000001");
    contract.receiveSubscription();
    contract.completeSubscription(LocalDateTime.now());
    contract.changeMobilePhoneLinkedDiscount(Collections.emptyList(), LocalDateTime.now().plusDays(10));
    contract.changeMobilePhoneLinkedDiscount(List.of(discountPolicies.get("DCM0000002")), LocalDateTime.now().plusDays(20));
    
    // when
    var calculationResult = contract.calculate(LocalDate.now().plusMonths(1)).get();
    
    // then
    var productCalculationResult = checkProductCalculationResult(calculationResult, "NMP0000001", 9900);
    assertThat(productCalculationResult.getDiscountCalculationResults()).hasSize(2);
    
    long expected = Math.round(9900 * 9.0 / DiscountPolicy.DIVIDE_BY_USE_DAYS * -1);
    checkDiscountCalculationResult(productCalculationResult, "DCM0000001", expected);
    long expected2 = Math.round(5000 * 11.0 / DiscountPolicy.DIVIDE_BY_USE_DAYS * -1);
    checkDiscountCalculationResult(productCalculationResult, "DCM0000002", expected2);
  }
  
  private static ProductCalculationResult checkProductCalculationResult(CalculationResult calculationResult, String productCode, long amount) {
    var productCalculationResult = calculationResult.getProductCalculationResult(productCode);
    assertThat(productCalculationResult.getProdAmountIncludeVat()).isEqualTo(amount);
    return productCalculationResult;
  }
  
  private static void checkDiscountCalculationResult(ProductCalculationResult productCalculationResult, String dcPolicyCode, long amount) {
    var discountCalculationResult = productCalculationResult.getDiscountCalculationResult(dcPolicyCode);
    assertThat(discountCalculationResult.getDcAmountIncludeVat()).isEqualTo(amount);
  }
}
