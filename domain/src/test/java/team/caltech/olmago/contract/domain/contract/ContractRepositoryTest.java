package team.caltech.olmago.contract.domain.contract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.domain.config.DomainTestConfig;
import team.caltech.olmago.contract.domain.contract.uzoopackage.UzooPackage;
import team.caltech.olmago.contract.domain.contract.uzoopackage.UzooPackageRepository;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.plm.discount.DiscountType;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;
import team.caltech.olmago.contract.domain.product.ProductSubscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(DomainTestConfig.class)
public class ContractRepositoryTest {
  @Autowired
  TestEntityManager em;
  
  @Autowired
  ContractRepository contractRepository;
  
  @Autowired
  UzooPackageRepository uzooPackageRepository;
  
  @Autowired
  ProductRepository productRepository;
  
  @Autowired
  DiscountPolicyRepository discountPolicyRepository;
  
  // 구현체가 domain안에 없고 service안에 있다보니 bean을 찾지 못해 오류 발생.. 흠. 어떻게 하지
  @MockBean
  CustomerServiceProxy customerServiceProxy;
  
  @Before
  public void setUp() {
  
  }
  
  @After
  public void tearDown() {
    contractRepository.deleteAll();
  }
  
  @Test
  public void 살아있는서비스만있을때_고객ID와해지서비스포함으로조회하면_모두반환된다() {
    // given & when
    LocalDateTime subRcvDtm = LocalDateTime.now();
    contractRepository.saveAll(
      List.of(
          ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true ),
          ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true),
          ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
          ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
          ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true ),
          ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
      )
    );
  
    // then
    assertThat(contractRepository.findByCustomerId(1L, true)).hasSize(3);
    assertThat(contractRepository.findByCustomerId(2L, true)).hasSize(2);
    assertThat(contractRepository.findByCustomerId(3L, true)).hasSize(1);
  }
  
  @Test
  public void 살아있는서비스와해지서비스섞여있을때_고객ID와해지서비스포함으로조회하면_모두반환된다() {
    // given
    LocalDateTime subRcvDtm = LocalDateTime.now();
    contractRepository.saveAll(
        List.of(
            ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
        )
    );
  
    List<Contract> termContracts = List.of(
      ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
      ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
      ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true )
    );
    contractRepository.saveAll(termContracts);
    
    // when (가입완료->해지접수->해지완료)
    termContracts.forEach(c -> c.completeSubscription(LocalDateTime.now()));
    termContracts.forEach(c -> c.receiveTermination(5L, LocalDateTime.now()));
    termContracts.forEach(c -> c.completeTermination(LocalDateTime.now()));
    
    // then
    assertThat(contractRepository.findByCustomerId(1L, true)).hasSize(3);
    assertThat(contractRepository.findByCustomerId(2L, true)).hasSize(2);
    assertThat(contractRepository.findByCustomerId(3L, true)).hasSize(1);
  }
  
  @Test
  public void 살아있는서비스와해지서비스섞여있을때_고객ID와해지서비스비포함으로조회() {
    // given
    LocalDateTime subRcvDtm = LocalDateTime.now();
    contractRepository.saveAll(
        List.of(
            ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
        )
    );
    
    List<Contract> termContracts = List.of(
        ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
        ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
        ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true )
    );
    contractRepository.saveAll(termContracts);
    
    // when (가입완료->해지접수->해지완료)
    termContracts.forEach(c -> c.completeSubscription(LocalDateTime.now()));
    termContracts.forEach(c -> c.receiveTermination(5L, LocalDateTime.now()));
    termContracts.forEach(c -> c.completeTermination(LocalDateTime.now()));

    // then
    assertThat(contractRepository.findByCustomerId(1L, false)).hasSize(2);
    assertThat(contractRepository.findByCustomerId(2L, false)).isEmpty();
    assertThat(contractRepository.findByCustomerId(3L, false)).hasSize(1);
  }
  
  @Test
  public void 계약ID와주문ID로조회() {
    // given & when
    LocalDateTime subRcvDtm = LocalDateTime.now();
    contractRepository.saveAll(
        List.of(
            ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true ),
            ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
        )
    );
  
    assertThat(contractRepository.findByCustomerAndOrderId(1L, 2L)).hasSize(2);
    assertThat(contractRepository.findByCustomerAndOrderId(1L, 4L)).hasSize(1);
    assertThat(contractRepository.findByCustomerAndOrderId(1L, 3L)).isEmpty();
  }
  
  @Test
  public void 패키지계약으로_옵션계약찾으면_성공() {
    // given & when
    LocalDateTime subRcvDtm = LocalDateTime.now();
    Contract pkgContract = ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true );
    Contract optContract = ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true);
    Contract unitContract = ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true );
    contractRepository.saveAll(
        List.of(
          pkgContract, optContract, unitContract
        )
    );
    
    UzooPackage pkg = UzooPackage.builder()
        .packageContract(pkgContract)
        .optionContract(optContract)
        .subscriptionReceivedDateTime(subRcvDtm)
        .build();
    uzooPackageRepository.save(pkg);
    
    // then
    assertThat(contractRepository.findOptionContractByPackageContract(pkgContract).get()).isEqualTo(optContract);
    assertThat(contractRepository.findOptionContractByPackageContract(optContract)).isEmpty();
    assertThat(contractRepository.findOptionContractByPackageContract(unitContract)).isEmpty();
  }
  
  @Test
  public void 해지된계약없을때_동일명의로같은상품쓰는지체크() {
    // given & when
    LocalDateTime subRcvDtm = LocalDateTime.now();
    contractRepository.saveAll(
        List.of(
            ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true ),
            ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
        )
    );
  
    // then
    assertThat(contractRepository.countActiveContractByCustomerAndFeeProductCode(1L, "NMP0000001")).isEqualTo(1);
    assertThat(contractRepository.countActiveContractByCustomerAndFeeProductCode(1L, "NMO0000002")).isEqualTo(0);
  }
  
  @Test
  public void 해지된계약존재할때_동일명의로같은상품쓰는지체크() {
    // given & when
    LocalDateTime subRcvDtm = LocalDateTime.now();
    Contract c1 = ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true );
    Contract c2 = ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true);
    contractRepository.saveAll(
        List.of(
            c1,
            c2,
            ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true ),
            ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
        )
    );
  
    List<Contract> termContracts = List.of(c1, c2);
    termContracts.forEach(c -> c.completeSubscription(LocalDateTime.now()));
    termContracts.forEach(c -> c.receiveTermination(5L, LocalDateTime.now()));
    termContracts.forEach(c -> c.completeTermination(LocalDateTime.now()));
    
    // then
    assertThat(contractRepository.countActiveContractByCustomerAndFeeProductCode(1L, "NMP0000001")).isEqualTo(0);
    assertThat(contractRepository.countActiveContractByCustomerAndFeeProductCode(1L, "NMO0000001")).isEqualTo(0);
    assertThat(contractRepository.countActiveContractByCustomerAndFeeProductCode(1L, "NMO0000002")).isEqualTo(0);
  }
  
  @Test
  public void 주어진종류의할인이_현재유효한계약에부여됐을때_카운트한다() {
    // given & when
    LocalDateTime subRcvDtm = LocalDateTime.now();
    List<Contract> contracts = contractRepository.saveAll(
        List.of(
            ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true ),
            ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
        )
    );
    
    assertThat(contractRepository.countAppliedDcTypeByCustomer(1L, DiscountType.THE_FIRST_SUBSCRIPTION)).isEqualTo(1);
    assertThat(contractRepository.countAppliedDcTypeByCustomer(3L, DiscountType.THE_FIRST_SUBSCRIPTION)).isEqualTo(0);
  }
  
  @Test
  public void 주어진종류의할인이_예전계약에부여됐을때도_카운트한다() {
    // given & when
    LocalDateTime subRcvDtm = LocalDateTime.now();
    List<Contract> termContracts = contractRepository.saveAll(
        List.of(
            ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true ),
            ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
        )
    );
    termContracts.forEach(c -> c.completeSubscription(LocalDateTime.now()));
    termContracts.forEach(c -> c.receiveTermination(5L, LocalDateTime.now()));
    termContracts.forEach(c -> c.completeTermination(LocalDateTime.now()));
    
    assertThat(contractRepository.countAppliedDcTypeByCustomer(1L, DiscountType.THE_FIRST_SUBSCRIPTION)).isEqualTo(1);
    assertThat(contractRepository.countAppliedDcTypeByCustomer(3L, DiscountType.THE_FIRST_SUBSCRIPTION)).isEqualTo(0);
  }
  
  @Test
  public void 계약단위조회_계약만단건조회() {
    LocalDateTime subRcvDtm = LocalDateTime.now();
    List<Contract> termContracts = contractRepository.saveAll(
        List.of(
            ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createUzoopassAllContract(2L, 3L, subRcvDtm, true ),
            ContractFixtures.createBaeminContract(2L, 3L, subRcvDtm, ContractType.OPTION, true),
            ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true ),
            ContractFixtures.createBaeminContract(3L, 5L, subRcvDtm, ContractType.UNIT, true)
        )
    );
    List<Contract> foundContracts = contractRepository.findByContractId(1L, false, false);
    assertThat(foundContracts).hasSize(1);
    assertThat(foundContracts.get(0).getId()).isEqualTo(1L);
  }
  
  @Test
  public void 계약단위조회_계약만_같이조회() {
    // given
    LocalDateTime subRcvDtm = LocalDateTime.now();
    Contract pkgContract = ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true );
    Contract optContract = ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true);
    Contract unitContract = ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true );
    contractRepository.saveAll(
        List.of(
            pkgContract,
            optContract,
            unitContract
        )
    );
    UzooPackage pkg = UzooPackage.builder()
        .packageContract(pkgContract)
        .optionContract(optContract)
        .subscriptionReceivedDateTime(subRcvDtm)
        .build();
    uzooPackageRepository.save(pkg);
    
    // when1
    List<Contract> foundContracts = contractRepository.findByContractId(
        pkgContract.getId(), true, false
    );
    // then1
    assertThat(foundContracts).hasSize(2);
    assertThat(foundContracts.get(0).getId()).isIn(pkgContract.getId(), optContract.getId());
    assertThat(foundContracts.get(1).getId()).isIn(pkgContract.getId(), optContract.getId());
  
    // when2
    foundContracts = contractRepository.findByContractId(
        optContract.getId(), true, false
    );
    // then2
    assertThat(foundContracts).hasSize(2);
    assertThat(foundContracts.get(0).getId()).isIn(pkgContract.getId(), optContract.getId());
    assertThat(foundContracts.get(1).getId()).isIn(pkgContract.getId(), optContract.getId());
  
    // when3
    foundContracts = contractRepository.findByContractId(
        unitContract.getId(), true, false
    );
    // then3
    assertThat(foundContracts).hasSize(1);
    assertThat(foundContracts.get(0).getId()).isEqualTo(unitContract.getId());
  }
  
  @Test
  public void 계약단위조회_계약과상품할인모두_단건조회() {
    // given
    LocalDateTime subRcvDtm = LocalDateTime.now();
    Contract pkgContract = ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true );
    Contract optContract = ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true);
    Contract unitContract = ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true );
    contractRepository.saveAll(
        List.of(
            pkgContract,
            optContract,
            unitContract
        )
    );
    UzooPackage pkg = UzooPackage.builder()
        .packageContract(pkgContract)
        .optionContract(optContract)
        .subscriptionReceivedDateTime(subRcvDtm)
        .build();
    uzooPackageRepository.save(pkg);
    
    em.clear();
    
    // when1
    List<Contract> foundContracts = contractRepository.findByContractId(
        pkgContract.getId(), false, true
    );
    // then1
    assertThat(foundContracts).hasSize(1);
    assertThat(foundContracts.get(0).getId()).isEqualTo(pkgContract.getId());
    assertThat(getAllProductCodes(foundContracts)).contains("NMP0000001", "NMB0000001", "NMB0000002");
    assertThat(getAllDCCodes(foundContracts)).contains("DCP0000001", "DCM0000001", "DCB0000001");
  }
  
  @Test
  public void 계약단위조회_계약과상품할인모두_같이조회() {
    // given
    LocalDateTime subRcvDtm = LocalDateTime.now();
    Contract pkgContract = ContractFixtures.createUzoopassAllContract(1L, 2L, subRcvDtm, true );
    Contract optContract = ContractFixtures.createBaeminContract(1L, 2L, subRcvDtm, ContractType.OPTION, true);
    Contract unitContract = ContractFixtures.createYanoljaContract(1L, 4L, subRcvDtm, ContractType.UNIT, true );
    contractRepository.saveAll(
        List.of(
            pkgContract,
            optContract,
            unitContract
        )
    );
    UzooPackage pkg = UzooPackage.builder()
        .packageContract(pkgContract)
        .optionContract(optContract)
        .subscriptionReceivedDateTime(subRcvDtm)
        .build();
    uzooPackageRepository.save(pkg);
    
    em.clear();
    
    // when1
    List<Contract> foundContracts = contractRepository.findByContractId(
        pkgContract.getId(), true, true
    );
    // then1
    assertThat(foundContracts).hasSize(2);
    assertThat(foundContracts.get(0).getId()).isIn(pkgContract.getId(), optContract.getId());
    assertThat(foundContracts.get(1).getId()).isIn(pkgContract.getId(), optContract.getId());
    assertThat(getAllProductCodes(foundContracts)).contains("NMP0000001", "NMB0000001", "NMB0000002", "NMO0000001");
    assertThat(getAllDCCodes(foundContracts)).contains("DCP0000001", "DCM0000001", "DCB0000001", "DCO0000003");
  }
  
  private List<String> getAllProductCodes(List<Contract> contracts) {
    return contracts.stream()
        .map(Contract::getProductSubscriptions)
        .flatMap(List::stream)
        .map(ProductSubscription::getProductCode)
        .collect(Collectors.toList());
  }
  
  private List<String> getAllDCCodes(List<Contract> contracts) {
    return contracts.stream()
        .map(Contract::getProductSubscriptions)
        .flatMap(List::stream)
        .map(ProductSubscription::getDiscountSubscriptions)
        .flatMap(List::stream)
        .map(dc -> dc.getDiscountPolicy().getDcPolicyCode())
        .collect(Collectors.toList());
  }
}
