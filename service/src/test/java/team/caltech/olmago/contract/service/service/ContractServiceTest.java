package team.caltech.olmago.contract.service.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;
import team.caltech.olmago.contract.service.dto.ContractDto;
import team.caltech.olmago.contract.service.dto.ReceiveContractSubscriptionDto;
import team.caltech.olmago.contract.service.message.out.MessageStore;
import team.caltech.olmago.contract.service.service.fixtures.PlmFixtures;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ContractServiceTest {
  protected static final Map<String, Product> products;
  protected static final Map<String, DiscountPolicy> discountPolicies;
  
  static {
    products = PlmFixtures.setupProducts().stream()
        .collect(Collectors.toMap(Product::getProductCode, p -> p));
    discountPolicies = PlmFixtures.setupDiscountPolicies().stream()
        .collect(Collectors.toMap(DiscountPolicy::getDcPolicyCode, d -> d));
  }
  
  @MockBean
  private MessageStore messageStore;
  
  @MockBean
  private ProductRepository productRepository;
  
  @Autowired
  private ContractService contractService;
  
  @Before
  public void setup() {
  
  }
  
  @After
  public void teardown() {
  }
  
  @Test
  public void 단품1건가입접수하면_가입접수1건됨() {
    // given
    String optProductCode = "NMO0000001";
    ReceiveContractSubscriptionDto dto = ReceiveContractSubscriptionDto.builder()
        .customerId(1L)
        .subRcvDtm(LocalDateTime.of(2022,9,1,1,1,1))
        .orderId(2L)
        .unitProdCds(List.of(optProductCode))
        .build();
  
    when(productRepository.findById(optProductCode)).thenReturn(Optional.of(products.get(optProductCode)));
    
    // when
    List<ContractDto> contracts = contractService.receiveContractSubscription(dto);
    
    // then
    assertThat(contracts.size()).isEqualTo(1);
  }
}
