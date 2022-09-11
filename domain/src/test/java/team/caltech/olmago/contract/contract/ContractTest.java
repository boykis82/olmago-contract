package team.caltech.olmago.contract.contract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.TestConfig;
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

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(TestConfig.class)
public class ContractTest {
  @Autowired
  private DiscountPolicyRepository discountPolicyRepository;
  
  @Autowired
  private ProductRepository productRepository;

  private PlmFixtures plmFixtures;
  private Map<String, Product> products;
  private Map<String, DiscountPolicy> discountPolicies;
  private LocalDateTime subRcvDtm;
  private LocalDateTime subCmplDtm;
  private LocalDateTime termRcvDtm;
  private LocalDateTime termRcvCnclDtm;
  private LocalDateTime termCmplDtm;
  
  @Before
  public void setUp() {
    products = PlmFixtures.setupProducts().stream().collect(Collectors.toMap(Product::getProductCode, p -> p));
    discountPolicies = PlmFixtures.setupDiscountPolicies().stream().collect(Collectors.toMap(DiscountPolicy::getDcPolicyCode, d -> d));
  
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
    Contract contract = createUzoopassAllContract();

    assertThat(contract.getCustomerId()).isEqualTo(1L);
    assertThat(contract.getLastOrderId()).isEqualTo(2L);
    assertThat(contract.getContractType()).isEqualTo(ContractType.PACKAGE);
    assertThat(contract.getFeeProductCode()).isEqualTo("NM00000001");
    assertThat(contract.getLifeCycle().getSubscriptionReceivedDateTime()).isEqualTo(subRcvDtm);
    assertThat(contract.getLifeCycle().getSubscriptionCompletedDateTime()).isNull();
    assertThat(contract.getLifeCycle().getTerminationReceivedDateTime()).isNull();
    assertThat(contract.getLifeCycle().getTerminationCompletedDateTime()).isNull();
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
  public void 계약접수된상태에서_접수완료되면_가입완료돼야함() {
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);
  
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
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);
    contract.receiveTermination(3L, termRcvDtm);

    assertThat(contract.getLastOrderId()).isEqualTo(3L);
    assertThat(contract.getLifeCycle().getSubscriptionReceivedDateTime()).isEqualTo(subRcvDtm);
    assertThat(contract.getLifeCycle().getSubscriptionCompletedDateTime()).isEqualTo(subCmplDtm);
    assertThat(contract.getLifeCycle().getTerminationReceivedDateTime()).isEqualTo(termRcvDtm);
    
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
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);
    contract.receiveTermination(2L, termRcvDtm);
    contract.cancelTerminationReceipt(3L, termRcvCnclDtm);
    
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
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);
    contract.receiveTermination(2L, termRcvDtm);
    contract.completeTermination(termCmplDtm);
    
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
  
  private Contract createUzoopassAllContract() {
    Contract contract = Contract.builder()
        .customerId(1L)
        .lastOrderId(2L)
        .contractType(ContractType.PACKAGE)
        .feeProductCode("NM00000001")
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

