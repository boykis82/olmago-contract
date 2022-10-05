package team.caltech.olmago.contract.service.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.common.message.MessageEnvelope;
import team.caltech.olmago.contract.common.message.MessageEnvelopeRepository;
import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.contract.uzoopackage.UzooPackageRepository;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;
import team.caltech.olmago.contract.service.dto.CompleteContractSubscriptionDto;
import team.caltech.olmago.contract.service.dto.ContractDto;
import team.caltech.olmago.contract.service.dto.ReceiveContractChangeDto;
import team.caltech.olmago.contract.service.dto.ReceiveContractSubscriptionDto;
import team.caltech.olmago.contract.service.message.out.MessageStore;
import team.caltech.olmago.contract.service.service.fixtures.PlmFixtures;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ContractServiceTest {
  @Autowired private ContractService contractService;
  @Autowired private MessageEnvelopeRepository messageEnvelopeRepository;
  @Autowired private ContractRepository contractRepository;
  @Autowired private UzooPackageRepository uzooPackageRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private DiscountPolicyRepository discountPolicyRepository;
  
  @Before
  public void setup() {
    List<Product> products = productRepository.findAll();
    List<DiscountPolicy> discountPolicies = discountPolicyRepository.findAll();
  }
  
  @After
  public void teardown() {
    uzooPackageRepository.deleteAll();
    contractRepository.deleteAll();
    messageEnvelopeRepository.deleteAll();
  }
  
  @Test
  public void 단품1건가입접수하면_가입접수1건됨() {
    // given
    ReceiveContractSubscriptionDto dto = receiveContractSubscriptionDtoBaseBuilder().unitProdCds(List.of("NMO0000001")).build();
    
    // when
    List<ContractDto> contracts = contractService.receiveContractSubscription(dto);
    List<MessageEnvelope> msgs = messageEnvelopeRepository.findByPublished(false);
    
    // then
    assertThat(contracts.size()).isEqualTo(1);
    assertThat(msgs.size()).isEqualTo(1);
    assertThat(msgs.get(0).getEventType()).isEqualTo("contractSubscriptionReceived");
  }
  
  @Test
  public void 단품3건가입접수하면_가입접수3건됨() {
    // given
    ReceiveContractSubscriptionDto dto = receiveContractSubscriptionDtoBaseBuilder().unitProdCds(List.of("NMO0000001", "NMO0000002", "NMO0000005")).build();
    
    // when
    List<ContractDto> contracts = contractService.receiveContractSubscription(dto);
    List<MessageEnvelope> msgs = messageEnvelopeRepository.findByPublished(false);
    
    // then
    assertThat(contracts.size()).isEqualTo(3);
    assertThat(msgs.size()).isEqualTo(3);
    assertThat(msgs.get(0).getEventType()).isEqualTo("contractSubscriptionReceived");
  }
  
  @Test
  public void 패키지가입접수하면_패키지옵션계약2건생성되고_가입완료하면유효한패키지생성됨() {
    // given
    ReceiveContractSubscriptionDto dto = receiveContractSubscriptionDtoBaseBuilder().pkgProdCd("NMP0000001").optProdCd("NMO0000001").unitProdCds(Collections.emptyList()).build();
    
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
    assertThat(msgs.get(0).getEventType()).isEqualTo("contractSubscriptionReceived");
    assertThat(msgs.get(1).getEventType()).isEqualTo("contractSubscriptionReceived");
    assertThat(msgs.get(2).getEventType()).isEqualTo("contractSubscriptionCompleted");
    assertThat(msgs.get(3).getEventType()).isEqualTo("contractSubscriptionCompleted");
    assertThat(uzooPackageRepository.findActivePackage(
        contractRepository.findById(c2.getContractId()).orElseThrow(),
        contractRepository.findById(c1.getContractId()).orElseThrow())
    ).isPresent();
  }
  
  @Test
  public void 패키지사용중_패키지상품만변경하면_계약상태에는변경없음() {
    // given
    ReceiveContractSubscriptionDto dto = receiveContractSubscriptionDtoBaseBuilder().pkgProdCd("NMP0000001").optProdCd("NMO0000001").unitProdCds(Collections.emptyList()).build();
    List<ContractDto> contracts = contractService.receiveContractSubscription(dto);
    CompleteContractSubscriptionDto cmplDto = completeContractSubscriptionDtoBaseBuilder(contracts.get(0).getContractId()).build();
    CompleteContractSubscriptionDto cmplDto2 = completeContractSubscriptionDtoBaseBuilder(contracts.get(1).getContractId()).build();
    ContractDto c1 = contractService.completeContractSubscription(cmplDto);
    ContractDto c2 = contractService.completeContractSubscription(cmplDto2);
    
    // when
    LocalDateTime chgDtm = LocalDateTime.of(2022,10,1,12,13,24);
    ReceiveContractChangeDto chgDto = ReceiveContractChangeDto.builder()
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
    // all, 구글one 해지접수
    assertThat(
        gatherProductCodesGivenPredicate(contracts.get(0), ps -> chgDtm.equals(ps.getTerminationReceivedDateTime()))
    ).contains("NMB0000001", "NMP0000001");
    // mini 가입접수
    assertThat(
        gatherProductCodesGivenPredicate(contracts.get(0), ps -> chgDtm.equals(ps.getSubscriptionReceivedDateTime()))
    ).contains("NMP0000003");
  }
  
  private ReceiveContractSubscriptionDto.ReceiveContractSubscriptionDtoBuilder receiveContractSubscriptionDtoBaseBuilder() {
    return ReceiveContractSubscriptionDto.builder()
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
