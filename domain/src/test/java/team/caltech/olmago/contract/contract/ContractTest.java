package team.caltech.olmago.contract.contract;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  public void givenPackage_whenCreateContract_thenShouldBeGetId() {
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
  public void givenSubRcvContract_whenSubCompleted_thenSubscriptionShouldBeCompleted() {
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
  public void givenSubCmplContract_whenTermRcv_thenSubscriptionShouldBeTerminationReceived() {
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);
    contract.receiveTermination(2L, termRcvDtm);
  
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
  public void givenTermRcvContract_whenCancelTermRcv_thenSubscriptionShouldBeActive() {
    Contract contract = createUzoopassAllContract();
    contract.completeSubscription(subCmplDtm);
    contract.receiveTermination(2L, termRcvDtm);
    contract.cancelTerminationReceipt(2L, termRcvCnclDtm);
    
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
  public void givenTermRcvContract_whenTermCompleted_thenSubscriptionShouldBeTerminated() {
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
    
    ProductSubscription ps1 = ProductSubscription.builder()
        .product(products.get("NMP0000001"))
        .contract(contract)
        .subscriptionReceivedDateTime(subRcvDtm)
        .build();
    ps1.discountSubscriptions(
        List.of(
            DiscountSubscription.builder().discountPolicy(discountPolicies.get("DCP0000001")).productSubscription(ps1).subRcvDtm(subRcvDtm).build(),
            DiscountSubscription.builder().discountPolicy(discountPolicies.get("DCM0000001")).productSubscription(ps1).subRcvDtm(subRcvDtm).build()
        )
    );
    
    ProductSubscription ps2 = ProductSubscription.builder()
        .product(products.get("NMB0000001"))
        .contract(contract)
        .subscriptionReceivedDateTime(contract.getLifeCycle().getSubscriptionReceivedDateTime())
        .build();
    ps2.discountSubscriptions(
        List.of(
            DiscountSubscription.builder().discountPolicy(discountPolicies.get("DCB0000001")).productSubscription(ps2).subRcvDtm(subRcvDtm).build()
        )
    );
    
    ProductSubscription ps3 = ProductSubscription.builder()
        .product(products.get("NMB0000002"))
        .contract(contract)
        .subscriptionReceivedDateTime(contract.getLifeCycle().getSubscriptionReceivedDateTime())
        .build();
    ps3.discountSubscriptions(
        List.of(
            DiscountSubscription.builder().discountPolicy(discountPolicies.get("DCB0000001")).productSubscription(ps3).subRcvDtm(subRcvDtm).build()
        )
    );
    
    contract.addProductSubscriptions(List.of(ps1, ps2, ps3));
    return contract;
  }
  
}

