package team.caltech.olmago.contract.service.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import team.caltech.olmago.common.message.MessageEnvelope;
import team.caltech.olmago.common.message.MessageEnvelopeRepository;
import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.contract.uzoopackage.UzooPackageRepository;
import team.caltech.olmago.contract.domain.customer.CustomerDto;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;
import team.caltech.olmago.contract.service.dto.CompleteContractSubscriptionDto;
import team.caltech.olmago.contract.service.dto.ContractDto;
import team.caltech.olmago.contract.service.message.in.command.order.ReceiveContractChangeCmd;
import team.caltech.olmago.contract.service.message.in.command.order.ReceiveContractSubscriptionCmd;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ContractServiceTest {
  @Autowired
  private ContractService contractService;
  @Autowired
  private MessageEnvelopeRepository messageEnvelopeRepository;
  @Autowired
  private ContractRepository contractRepository;
  @Autowired
  private UzooPackageRepository uzooPackageRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private DiscountPolicyRepository discountPolicyRepository;
  @MockBean
  private CustomerServiceProxy customerServiceProxy;
  
  @Before
  public void setup() {
    List<Product> products = productRepository.findAll();
    List<DiscountPolicy> discountPolicies = discountPolicyRepository.findAll();
  
    CustomerDto customerDto = CustomerDto.builder()
        .customerId(1L)
        .mobilePhonePricePlan("PLATINUM")
        .dcTargetUzooPassProductCodes(Collections.emptyList())
        .build();
    
    given(customerServiceProxy.findByCustomerId(1L))
        .willReturn(Mono.just(customerDto));
  }
  
  @After
  public void teardown() {
    uzooPackageRepository.deleteAll();
    contractRepository.deleteAll();
    messageEnvelopeRepository.deleteAll();
  }
  
  @Test
  public void ??????1?????????????????????_????????????1??????() {
    // given
    ReceiveContractSubscriptionCmd dto = receiveContractSubscriptionDtoBaseBuilder().unitProdCds(List.of("NMO0000001")).build();
    
    // when
    List<ContractDto> contracts = contractService.receiveContractSubscription(dto);
    List<MessageEnvelope> msgs = messageEnvelopeRepository.findByPublished(false);
    
    // then
    assertThat(contracts.size()).isEqualTo(1);
    assertThat(msgs.size()).isEqualTo(1);
    assertThat(msgs.get(0).getMessageName()).isEqualTo("contractSubscriptionReceived");
  }
  
  @Test
  public void ??????3?????????????????????_????????????3??????() {
    // given
    ReceiveContractSubscriptionCmd dto = receiveContractSubscriptionDtoBaseBuilder().unitProdCds(List.of("NMO0000001", "NMO0000002", "NMO0000005")).build();
    
    // when
    List<ContractDto> contracts = contractService.receiveContractSubscription(dto);
    List<MessageEnvelope> msgs = messageEnvelopeRepository.findByPublished(false);
    
    // then
    assertThat(contracts.size()).isEqualTo(3);
    assertThat(msgs.size()).isEqualTo(3);
    assertThat(msgs.get(0).getMessageName()).isEqualTo("contractSubscriptionReceived");
  }
  
  @Test
  public void ???????????????????????????_?????????????????????2???????????????_?????????????????????????????????????????????() {
    // given
    ReceiveContractSubscriptionCmd dto = receiveContractSubscriptionDtoBaseBuilder().pkgProdCd("NMP0000001").optProdCd("NMO0000001").unitProdCds(Collections.emptyList()).build();
    
    // when
    List<ContractDto> contracts = contractService.receiveContractSubscription(dto);
    CompleteContractSubscriptionDto cmplDto = completeContractSubscriptionDtoBaseBuilder(contracts.get(0).getContractId()).build();
    CompleteContractSubscriptionDto cmplDto2 = completeContractSubscriptionDtoBaseBuilder(contracts.get(1).getContractId()).build();
    ContractDto c1 = contractService.completeContractSubscription(cmplDto);
    ContractDto c2 = contractService.completeContractSubscription(cmplDto2);
    
    List<MessageEnvelope> msgs = messageEnvelopeRepository.findByPublished(false);
    
    // then
    assertThat(contracts.size()).isEqualTo(2);
    assertThat(msgs.size()).isEqualTo(4);
    assertThat(msgs.get(0).getMessageName()).isEqualTo("contractSubscriptionReceived");
    assertThat(msgs.get(1).getMessageName()).isEqualTo("contractSubscriptionReceived");
    assertThat(msgs.get(2).getMessageName()).isEqualTo("contractSubscriptionCompleted");
    assertThat(msgs.get(3).getMessageName()).isEqualTo("contractSubscriptionCompleted");
    assertThat(uzooPackageRepository.findActivePackage(
        contractRepository.findById(c2.getContractId()).orElseThrow(),
        contractRepository.findById(c1.getContractId()).orElseThrow())
    ).isPresent();
  }
  
  @Test
  public void ??????????????????_??????????????????????????????_??????????????????????????????() {
    // given
    ReceiveContractSubscriptionCmd dto = receiveContractSubscriptionDtoBaseBuilder().pkgProdCd("NMP0000001").optProdCd("NMO0000001").unitProdCds(Collections.emptyList()).build();
    List<ContractDto> contracts = contractService.receiveContractSubscription(dto);
    CompleteContractSubscriptionDto cmplDto = completeContractSubscriptionDtoBaseBuilder(contracts.get(0).getContractId()).build();
    CompleteContractSubscriptionDto cmplDto2 = completeContractSubscriptionDtoBaseBuilder(contracts.get(1).getContractId()).build();
    ContractDto c1 = contractService.completeContractSubscription(cmplDto);
    ContractDto c2 = contractService.completeContractSubscription(cmplDto2);
    
    // when
    LocalDateTime chgDtm = LocalDateTime.of(2022,10,1,12,13,24);
    ReceiveContractChangeCmd chgDto = ReceiveContractChangeCmd.builder()
        .changeReceivedDateTime(chgDtm)
        .orderId(3L)
        .customerId(1L)
        .packageContractId(c2.getContractId())
        .beforePackageProductCode("NMP0000001")
        .beforeOptionProductCode("NMO0000001")
        .afterPackageProductCode("NMP0000003")
        .build();
    contracts = contractService.receiveContractChange(chgDto);
    
    // then
    assertThat(contracts.size()).isEqualTo(1);
    // all, ??????one ????????????
    assertThat(
        gatherProductCodesGivenPredicate(contracts.get(0), ps -> chgDtm.equals(ps.getTerminationReceivedDateTime()))
    ).contains("NMB0000001", "NMP0000001");
    // mini ????????????
    assertThat(
        gatherProductCodesGivenPredicate(contracts.get(0), ps -> chgDtm.equals(ps.getSubscriptionReceivedDateTime()))
    ).contains("NMP0000003");
  }
  
  private ReceiveContractSubscriptionCmd.ReceiveContractSubscriptionCmdBuilder receiveContractSubscriptionDtoBaseBuilder() {
    return ReceiveContractSubscriptionCmd.builder()
        .customerId(1L)
        .subRcvDtm(LocalDateTime.of(2022,9,1,1,1,1))
        .orderId(2L);
  }
  
  private CompleteContractSubscriptionDto.CompleteContractSubscriptionDtoBuilder completeContractSubscriptionDtoBaseBuilder(long contractId) {
    return CompleteContractSubscriptionDto.builder()
        .contractId(contractId)
        .subscriptionCompletedDateTime(LocalDateTime.now());
  }
  
  private List<String> gatherProductCodesGivenPredicate(ContractDto contract, Predicate<ContractDto.ProductSubscriptionDto> predicate) {
    return contract.getProductSubscriptions().stream()
        .filter(predicate)
        .map(ContractDto.ProductSubscriptionDto::getProductCode)
        .collect(Collectors.toList());
  }
}
