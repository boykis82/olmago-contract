package team.caltech.olmago.contract.contract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import team.caltech.olmago.contract.discount.DiscountSubscription;
import team.caltech.olmago.contract.plm.*;
import team.caltech.olmago.contract.product.ProductSubscription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
//@DataJpaTest
//@Import(TestConfig.class)
public class ContractTest {
  private Map<String, Product> products;
  private Map<String, DiscountPolicy> discountPolicies;

  private LocalDateTime subRcvDtm;
  private LocalDateTime subCmplDtm;
  private LocalDateTime termRcvDtm;
  private LocalDateTime termRcvCnclDtm;
  private LocalDateTime termCmplDtm;
  
  @Before
  public void setUp() {
    products = PlmFixtures.setupProducts().stream()
        .collect(Collectors.toMap(Product::getProductCode, p -> p));
    discountPolicies = PlmFixtures.setupDiscountPolicies().stream()
        .collect(Collectors.toMap(DiscountPolicy::getDcPolicyCode, d -> d));
  
    subRcvDtm = LocalDateTime.of(2022,9,1,10,23,12);
    subCmplDtm = LocalDateTime.of(2022,9,1,11,12,23);
    termRcvDtm = LocalDateTime.of(2022,9,12,11,12,23);
    termRcvCnclDtm = LocalDateTime.of(2022,9,16,11,12,23);
    termCmplDtm = LocalDateTime.of(2022,10,1,1,0,2);
  }

  @After
  public void tearDown() {

  }
  
  @Test
  public void 최초에_계약정보를접수하면_ID가채번되고접수일시로값이생성되어야함() {
    // given & when
    Contract contract = createUzoopassAllContract();

    // then
    assertThat(contract.getCustomerId()).isEqualTo(1L);
    assertThat(contract.getLastOrderId()).isEqualTo(2L);
    assertThat(contract.getContractType()).isEqualTo(ContractType.PACKAGE);
    assertThat(contract.getFeeProductCode()).isEqualTo("NMP0000001");
    assertThat(contract.getLifeCycle().isSubscriptionReceived()).isTrue();
    assertThat(contract.getLifeCycle().getSubscriptionReceivedDateTime()).isEqualTo(subRcvDtm);
    assertThat(contract.getBillCycle()).isNull();
    assertThat(contract.getLastRegularPaymentCompletedDateTime()).isNull();
    assertThat(contract.getUnitContractConvertedDateTime()).isNull();
    
    assertThat(
        contract.getProductSubscriptions().stream()
            .allMatch(ps -> ps.getLifeCycle().getSubscriptionReceivedDateTime().equals(subRcvDtm))
    ).isTrue();
    assertThat(
        contract.getProductSubscriptions().stream()
            .map(ProductSubscription::getDiscountSubscriptions)
            .flatMap(List::stream)
            .allMatch(ds -> ds.getLifeCycle().getSubscriptionReceivedDateTime().equals(subRcvDtm))
    ).isTrue();
  }
  
  @Test
  public void 계약접수된상태에서_접수취소되면_클리어돼야함() {
    // given & when
    Contract contract = createUzoopassAllContract();
    LocalDateTime cnclDtm = LocalDateTime.of(2022,9,2,0,0,1);
    contract.cancelSubscriptionReceipt(cnclDtm);
    
    // then
    assertThat(contract.getLifeCycle().isSubscriptionReceived()).isFalse();
    
    assertThat(
        contract.getProductSubscriptions().stream()
            .allMatch(ps -> ps.getLifeCycle().getCancelSubscriptionReceiptDateTime().equals(cnclDtm))
    ).isTrue();
    assertThat(
        contract.getProductSubscriptions().stream()
            .map(ProductSubscription::getDiscountSubscriptions)
            .flatMap(List::stream)
            .allMatch(ds -> ds.getLifeCycle().getCancelSubscriptionReceiptDateTime().equals(cnclDtm))
    ).isTrue();
  }
  
  @Test
  public void 계약접수된상태에서_접수완료되면_가입완료돼야함() {
    // given
    Contract contract = createUzoopassAllContract();

    // when
    contract.completeSubscription(subCmplDtm);

    // then
    assertThat(contract.getLifeCycle().getSubscriptionReceivedDateTime()).isEqualTo(subRcvDtm);
    assertThat(contract.getLifeCycle().getSubscriptionCompletedDateTime()).isEqualTo(subCmplDtm);
    assertThat(contract.getBillCycle().getTheFirstBillStartDate()).isEqualTo(subCmplDtm.toLocalDate());
    assertThat(contract.getBillCycle().getCurrentBillStartDate()).isEqualTo(subCmplDtm.toLocalDate());
    assertThat(contract.getBillCycle().getMonthsPassed()).isEqualTo(0);
    assertThat(contract.getBillCycle().getCurrentBillEndDate()).isEqualTo(LocalDate.of(2022,9,30));
    
    assertThat(
        contract.getProductSubscriptions().stream()
            .allMatch(ps -> ps.getLifeCycle().getSubscriptionCompletedDateTime().equals(subCmplDtm))
    ).isTrue();
    assertThat(
        contract.getProductSubscriptions().stream()
            .map(ProductSubscription::getDiscountSubscriptions)
            .flatMap(List::stream)
            .allMatch(ds -> ds.getLifeCycle().getSubscriptionCompletedDateTime().equals(subCmplDtm))
    ).isTrue();
  }
  
  @Test
  public void 계약완료된상태에서_해지접수되면_해지접수돼야함() {
    // given
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);

    // when
    contract.receiveTermination(3L, termRcvDtm);
    assertThat(contract.getLastOrderId()).isEqualTo(3L);
    assertThat(contract.getLifeCycle().getSubscriptionReceivedDateTime()).isEqualTo(subRcvDtm);
    assertThat(contract.getLifeCycle().getSubscriptionCompletedDateTime()).isEqualTo(subCmplDtm);
    assertThat(contract.getLifeCycle().getTerminationReceivedDateTime()).isEqualTo(termRcvDtm);

    // then
    assertThat(
        contract.getProductSubscriptions().stream()
            .allMatch(ps -> ps.getLifeCycle().getTerminationReceivedDateTime().equals(termRcvDtm))
    ).isTrue();
    assertThat(
        contract.getProductSubscriptions().stream()
            .map(ProductSubscription::getDiscountSubscriptions)
            .flatMap(List::stream)
            .allMatch(ds -> ds.getLifeCycle().getTerminationReceivedDateTime().equals(termRcvDtm))
    ).isTrue();
  }
  
  @Test
  public void 계약해지접수된상테에서_해지접수취소하면_계약이살아나야함() {
    // given
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);
    contract.receiveTermination(2L, termRcvDtm);

    // when
    contract.cancelTerminationReceipt(3L, termRcvCnclDtm);

    // then
    assertThat(contract.getLifeCycle().getSubscriptionReceivedDateTime()).isEqualTo(subRcvDtm);
    assertThat(contract.getLifeCycle().getSubscriptionCompletedDateTime()).isEqualTo(subCmplDtm);
    assertThat(contract.getLifeCycle().getTerminationReceivedDateTime()).isNull();
    assertThat(contract.getLifeCycle().getCancelTerminationReceiptDateTime()).isEqualTo(termRcvCnclDtm);
    
    assertThat(
        contract.getProductSubscriptions().stream()
            .allMatch(ps -> ps.getLifeCycle().getCancelTerminationReceiptDateTime().equals(termRcvCnclDtm))
    ).isTrue();
    assertThat(
        contract.getProductSubscriptions().stream()
            .map(ProductSubscription::getDiscountSubscriptions)
            .flatMap(List::stream)
            .allMatch(ds -> ds.getLifeCycle().getCancelTerminationReceiptDateTime().equals(termRcvCnclDtm))
    ).isTrue();
  }
  
  
  @Test
  public void 해지접수된상태에서_해지완료되면_계약은끝나야함() {
    // given
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);
    contract.receiveTermination(2L, termRcvDtm);

    // when
    contract.completeTermination(termCmplDtm);

    // then
    assertThat(contract.getLifeCycle().getSubscriptionReceivedDateTime()).isEqualTo(subRcvDtm);
    assertThat(contract.getLifeCycle().getSubscriptionCompletedDateTime()).isEqualTo(subCmplDtm);
    assertThat(contract.getLifeCycle().getTerminationReceivedDateTime()).isEqualTo(termRcvDtm);
    assertThat(contract.getLifeCycle().getTerminationCompletedDateTime()).isEqualTo(termCmplDtm);
    
    assertThat(
        contract.getProductSubscriptions().stream()
            .allMatch(ps -> ps.getLifeCycle().getTerminationCompletedDateTime().equals(termCmplDtm))
    ).isTrue();
    assertThat(
        contract.getProductSubscriptions().stream()
            .map(ProductSubscription::getDiscountSubscriptions)
            .flatMap(List::stream)
            .allMatch(ds -> ds.getLifeCycle().getTerminationCompletedDateTime().equals(termCmplDtm))
    ).isTrue();
  }

  @Test
  public void 정기결제완료되면_과금주기갱신됨() {
    // given
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);

    // when - 첫 정기결제 완료
    contract.completeRegularPayment(LocalDateTime.of(2022,10,1,13,0,3));

    // then
    assertThat(contract.getBillCycle().getTheFirstBillStartDate()).isEqualTo(LocalDate.of(2022,9,1));
    assertThat(contract.getBillCycle().getCurrentBillStartDate()).isEqualTo(LocalDate.of(2022,10,1));
    assertThat(contract.getBillCycle().getCurrentBillEndDate()).isEqualTo(LocalDate.of(2022,10,31));
    assertThat(contract.getBillCycle().getMonthsPassed()).isEqualTo(1);

    // when - 두번쨰 정기결제 완료
    contract.completeRegularPayment(LocalDateTime.of(2022,11,1,13,1,13));

    // then
    assertThat(contract.getBillCycle().getTheFirstBillStartDate()).isEqualTo(LocalDate.of(2022,9,1));
    assertThat(contract.getBillCycle().getCurrentBillStartDate()).isEqualTo(LocalDate.of(2022,11,1));
    assertThat(contract.getBillCycle().getCurrentBillEndDate()).isEqualTo(LocalDate.of(2022,11,30));
    assertThat(contract.getBillCycle().getMonthsPassed()).isEqualTo(2);
  }

  /*
  all (해지접수)
    11번가 (해지접수)
    구글원 (해지접수)
  ->
  life (가입접수)
    세븐일레븐 (가입접수)
    투썸s (가입접수)
  */
  @Test
  public void 패키지계약유지중_구성품이겹치지않는상품으로변경하면_기존기본혜택은모두해지접수되고신규기본혜택은가입접수됨() {
    // given
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);

    // when
    LocalDateTime changeRcvDtm = LocalDateTime.of(2022,9,20,11,12,23);
    List<String> termProductCodes = contract.getProductSubscriptions().stream()
        .map(ProductSubscription::getProductCode)
        .collect(Collectors.toList());

    contract.changeContract(
        3L,
        "NMP0000002",
        termProductCodes,
        List.of(
            createProductSubscription(contract, "NMP0000002", changeRcvDtm),
            createProductSubscription(contract, "NMB0000003", changeRcvDtm),
            createProductSubscription(contract, "NMB0000004", changeRcvDtm)
        ),
        changeRcvDtm
    );

    // then
    // 해지접수상품 검증
    assertThat(
        contract.getProductSubscriptions().stream()
            .filter(ps -> changeRcvDtm.equals(ps.getLifeCycle().getTerminationReceivedDateTime()))
            .map(ProductSubscription::getProductCode)
            .collect(Collectors.toList())
    ).contains("NMP0000001", "NMB0000001", "NMB0000002");
    // 가입접수상품 검증
    assertThat(
        contract.getProductSubscriptions().stream()
            .filter(ps -> changeRcvDtm.equals(ps.getLifeCycle().getSubscriptionReceivedDateTime()))
            .map(ProductSubscription::getProductCode)
            .collect(Collectors.toList())
    ).contains("NMP0000002", "NMB0000003", "NMB0000004");
  }

  /*
  all (해지접수)
    11번가
    구글원
  ->
  mini (가입접수)
    11번가 (유지)
    구글원 (유지)
 */
  @Test
  public void 패키지계약유지중_구성품이일부겹치는상품으로변경하면_기존기본혜택중겹치는건해지접수되고_겹치지않는건유지되고_겹치치않는신규기본혜택은가입접수됨() {
    // given
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);

    // when
    LocalDateTime changeRcvDtm = LocalDateTime.of(2022,9,20,11,12,23);
    List<String> termProductCodes = List.of("NMP0000001");

    contract.changeContract(
        3L,
        "NMP0000003",
        termProductCodes,
        List.of(
            createProductSubscription(contract, "NMP0000003", changeRcvDtm)
        ),
        changeRcvDtm
    );
    // then
    // 해지접수상품 검증
    assertThat(
        contract.getProductSubscriptions().stream()
            .filter(ps -> changeRcvDtm.equals(ps.getLifeCycle().getTerminationReceivedDateTime()))
            .map(ProductSubscription::getProductCode)
            .collect(Collectors.toList())
    ).contains("NMP0000001");
    // 가입접수상품 검증
    assertThat(
        contract.getProductSubscriptions().stream()
            .filter(ps -> changeRcvDtm.equals(ps.getLifeCycle().getSubscriptionReceivedDateTime()))
            .map(ProductSubscription::getProductCode)
            .collect(Collectors.toList())
    ).contains("NMP0000003");
    // 유지상품 검증
    assertThat(
        contract.getProductSubscriptions().stream()
            .filter(ps -> ps.getLifeCycle().isSubscriptionCompleted())
            .map(ProductSubscription::getProductCode)
            .collect(Collectors.toList())
    ).contains("NMB0000001", "NMB0000002");
  }

  private Contract createUzoopassAllContract() {
    Contract contract = Contract.builder()
        .customerId(1L)
        .lastOrderId(2L)
        .contractType(ContractType.PACKAGE)
        .feeProductCode("NMP0000001")
        .subRcvDtm(subRcvDtm)
        .build();
    
    contract.addProductSubscriptions(List.of(
        createProductSubscription(contract, "NMP0000001", "DCP0000001", "DCM0000001"),
        createProductSubscription(contract, "NMB0000001", "DCB0000001"),
        createProductSubscription(contract, "NMB0000002", "DCB0000001")
    ));
    return contract;
  }

  private ProductSubscription createProductSubscription(Contract contract, String productCode, String ... dcCodes) {
    return createProductSubscription(contract, productCode, contract.getLifeCycle().getSubscriptionReceivedDateTime(), dcCodes);
  }

  private ProductSubscription createProductSubscription(Contract contract, String productCode, LocalDateTime subRcvDtm, String ... dcCodes) {
    ProductSubscription ps = ProductSubscription.builder()
        .product(products.get(productCode))
        .contract(contract)
        .subscriptionReceivedDateTime(subRcvDtm)
        .build();

    ps.discountSubscriptions(
        Arrays.stream(dcCodes)
            .map(dcCode -> DiscountSubscription.builder()
                .discountPolicy(discountPolicies.get(dcCode))
                .productSubscription(ps)
                .subRcvDtm(subRcvDtm)
                .build()
            )
            .collect(Collectors.toList())
    );
    return ps;
  }
}

