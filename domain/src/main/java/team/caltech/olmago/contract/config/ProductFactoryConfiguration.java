package team.caltech.olmago.contract.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.discount.condition.detail.*;
import team.caltech.olmago.contract.plm.Product;
import team.caltech.olmago.contract.plm.ProductRepository;
import team.caltech.olmago.contract.product.factory.ProductRelationRepository;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.product.factory.ProductFactory;
import team.caltech.olmago.contract.product.factory.ProductFactoryMap;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static team.caltech.olmago.contract.discount.condition.DiscountCondition.and;
import static team.caltech.olmago.contract.discount.condition.DiscountCondition.neg;
import static team.caltech.olmago.contract.product.factory.ProductRelation.ProductRelationType.*;
import static team.caltech.olmago.contract.customer.MobilePhonePricePlan.*;

@Configuration
public class ProductFactoryConfiguration {
  @Bean
  public ProductFactory uzooPassAllProductFactory(ContractRepository contractRepository,
                                           CustomerServiceProxy customerServiceProxy,
                                           ProductRepository productRepository,
                                           ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000001";
    List<String> optionProducts = findOptionProducts(productRelationRepository, productId);
    List<String> theFirstSubscriptionDcExceptProducts = findProductsExceptTheFirstSubscriptionDc(productRepository, optionProducts);
    
    return new ProductFactory(productId)
        .basicBenefitProductFactories(
            googleOneAllProductFactory(),
            amazonFreeDeliveryProductFactory()
        )
        .availableOptionProducts(optionProducts)
        .availableDiscountConditions(
            // 최초가입여부 and !특정 옵션 제외
            and(
                FirstSubscriptionDcCond.with(contractRepository),
                neg(RelatedOptionProductDcCond.with(contractRepository).in(theFirstSubscriptionDcExceptProducts))
            ).discountPolicyIds("DCP0000001"),
            // 플래티넘, 프라임플러스
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PLATINUM, PRIME_PLUS).discountPolicyIds("DCM0000001"),
            // 프라임
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME).discountPolicyIds("DCM0000002"),
            // 프라임 and 프로모션기간 내 가입
            and(MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME),
                SubscribedDateRangeDcCond.between(LocalDate.of(2022,9,1), LocalDate.of(2022,12,31))
            ).discountPolicyIds("DCM0000003")
        );
  }

  @Bean
  public ProductFactory uzooPassLifeProductFactory(ContractRepository contractRepository,
                                            CustomerServiceProxy customerServiceProxy,
                                            ProductRepository productRepository,
                                            ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000002";
    List<String> optionProducts = findOptionProducts(productRelationRepository, productId);
    List<String> theFirstSubscriptionDcExceptProducts = findProductsExceptTheFirstSubscriptionDc(productRepository, optionProducts);
    
    return new ProductFactory(productId)
        .basicBenefitProductFactories(
            sevenElevenProductFactory(),
            twosomePlaceProductFactory()
        )
        .availableOptionProducts(
            findOptionProducts(productRelationRepository, productId)
        )
        .availableDiscountConditions(
            // 최초가입여부 and !특정 옵션 제외
            and(
                FirstSubscriptionDcCond.with(contractRepository),
                neg(RelatedOptionProductDcCond.with(contractRepository).in(theFirstSubscriptionDcExceptProducts))
            ).discountPolicyIds("DCP0000001"),
            // 플래티넘, 프라임플러스
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PLATINUM, PRIME_PLUS).discountPolicyIds("DCM0000001"),
            // 프라임
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME).discountPolicyIds("DCM0000002"),
            // 프라임 and 프로모션기간 내 가입
            and(MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME),
                SubscribedDateRangeDcCond.between(LocalDate.of(2022,9,1), LocalDate.of(2022,12,31))
            ).discountPolicyIds("DCM0000003")
        );
  }
 
  @Bean
  public ProductFactory uzooPassMiniProductFactory(ContractRepository contractRepository, ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000003";
  
    return new ProductFactory(productId)
        .basicBenefitProductFactories(
            amazonFreeDeliveryProductFactory()
        )
        .availableOptionProducts(
            findOptionProducts(productRelationRepository, productId)
        )
        .availableDiscountConditions(
            FirstSubscriptionDcCond.with(contractRepository).discountPolicyIds("DCP0000002")
        );
  }
  
  private List<String> findOptionProducts(ProductRelationRepository productRelationRepository, String productId) {
    return productRelationRepository.findByMainProductAndProductRelationType(productId, PACKAGE_AND_OPTION, LocalDate.now());
  }
  
  private List<String> findProductsExceptTheFirstSubscriptionDc(ProductRepository productRepository, List<String> optionProducts) {
    return productRepository.findAllById(optionProducts).stream()
        .filter(p -> !p.isTheFirstSubscriptionDcTarget())
        .map(Product::getProductCode)
        .collect(Collectors.toList());
  }
  
  @Bean
  public ProductFactory uzooPassSlimProductFactory(ContractRepository contractRepository) {
    String productId = "NMP0000004";
    
    return new ProductFactory(productId)
        .basicBenefitProductFactories(
            amazonFreeDeliveryProductFactory()
        )
        .availableDiscountConditions(
            FirstSubscriptionDcCond.with(contractRepository).discountPolicyIds("DCP0000003")
        );
  }
  
  @Bean
  public ProductFactory googleOneAllProductFactory() {
    return new ProductFactory("NMB0000001")
        .availableDiscountConditions(
            ContractTypeDcCond.asPackage().discountPolicyIds("DCB0000001")
        );
  }
  
  
  @Bean
  public ProductFactory amazonFreeDeliveryProductFactory() {
    return new ProductFactory("NMB0000002")
        .availableDiscountConditions(
            ContractTypeDcCond.asPackage().discountPolicyIds("DCB0000001")
        );
  }
  
  @Bean
  public ProductFactory sevenElevenProductFactory() {
    return new ProductFactory("NMB0000003");
  }
  
  @Bean
  public ProductFactory twosomePlaceProductFactory() {
    return new ProductFactory("NMB0000004");
  }
  
  @Bean
  public ProductFactory baeminProductFactory(ContractRepository contractRepository) {
    return new ProductFactory("NMO0000001")
        .availableDiscountConditions(
            ContractTypeDcCond.asOption().discountPolicyIds("DCO0000003"),
            and(ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicyIds("DCU0000009")
        );
  }
  
  @Bean
  public ProductFactory goobneProductFactory() {
    return new ProductFactory("NMO0000002")
        .availableDiscountConditions(
            ContractTypeDcCond.asOption().discountPolicyIds("DCO0000001")
        );
  }
  
  @Bean
  public ProductFactory floAndDataProductFactory(ContractRepository contractRepository, CustomerServiceProxy customerServiceProxy) {
    return new ProductFactory("NMO0000004")
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption().discountPolicyIds("DCO0000001"),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicyIds("DCU0000002"),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PLATINUM, MAX, PRIME_PLUS)
            ).discountPolicyIds("DCU0000004"),
            // 프라임, 스페셜 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME, SPECIAL)
            ).discountPolicyIds("DCU0000005")
        );
  }
  
  @Bean
  public ProductFactory floAndDataPlusProductFactory(ContractRepository contractRepository, CustomerServiceProxy customerServiceProxy) {
    return new ProductFactory("NMO0000008")
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption().discountPolicyIds("DCO0000001"),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicyIds("DCU0000003"),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PLATINUM)
            ).discountPolicyIds("DCU0000006"),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME_PLUS, MAX)
            ).discountPolicyIds("DCU0000007"),
            // 프라임, 스페셜 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME, SPECIAL)
            ).discountPolicyIds("DCU0000008")
        );
  }
  
  @Bean
  public ProductFactory gamepassUltimateProductFactory(ContractRepository contractRepository) {
    return new ProductFactory("NMO0000005")
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption().discountPolicyIds("DCO0000002"),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicyIds("DCU0000001")
        );
  }
  
  
  @Bean
  public ProductFactory googleOneMiniProductFactory() {
    return new ProductFactory("NMO0000006")
        .availableDiscountConditions(
            ContractTypeDcCond.asOption().discountPolicyIds("DCO0000001")
        );
  }
  
  @Bean
  public ProductFactory yanoljaProductFactory() {
    return new ProductFactory("NMO0000010")
        .availableDiscountConditions(
            ContractTypeDcCond.asOption().discountPolicyIds("DCO0000001")
        );
  }
  
  @Bean
  public ProductFactoryMap productFactoryMap(ContractRepository contractRepository,
                                      CustomerServiceProxy customerServiceProxy,
                                      ProductRepository productRepository,
                                      ProductRelationRepository productRelationRepository) {
    ProductFactoryMap productFactoryMap = new ProductFactoryMap();
    // package
    productFactoryMap.put(uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, productRelationRepository));
    productFactoryMap.put(uzooPassLifeProductFactory(contractRepository, customerServiceProxy, productRepository, productRelationRepository));
    productFactoryMap.put(uzooPassMiniProductFactory(contractRepository, productRelationRepository));
    productFactoryMap.put(uzooPassSlimProductFactory(contractRepository));
    
    // 기본혜택
    productFactoryMap.put(googleOneAllProductFactory());
    productFactoryMap.put(amazonFreeDeliveryProductFactory());
    productFactoryMap.put(sevenElevenProductFactory());
    productFactoryMap.put(twosomePlaceProductFactory());
    
    // 옵션 or 단품
    productFactoryMap.put(baeminProductFactory(contractRepository));
    productFactoryMap.put(goobneProductFactory());
    productFactoryMap.put(floAndDataProductFactory(contractRepository, customerServiceProxy));
    productFactoryMap.put(floAndDataPlusProductFactory(contractRepository, customerServiceProxy));
    productFactoryMap.put(gamepassUltimateProductFactory(contractRepository));
    productFactoryMap.put(googleOneMiniProductFactory());
    productFactoryMap.put(yanoljaProductFactory());
    
    return productFactoryMap;
  }
}
