package team.caltech.olmago.contract.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.discount.condition.detail.*;
import team.caltech.olmago.contract.plm.DiscountPolicyRepository;
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
@Lazy
public class ProductFactoryConfiguration {
  @Bean
  public ProductFactory uzooPassAllProductFactory(ContractRepository contractRepository,
                                                  CustomerServiceProxy customerServiceProxy,
                                                  ProductRepository productRepository,
                                                  DiscountPolicyRepository discountPolicyRepository,
                                                  ProductRelationRepository productRelationRepository
  ) {
    String productId = "NMP0000001";
    List<String> optionProducts = findOptionProducts(productRelationRepository, productId);
    List<String> theFirstSubscriptionDcExceptProducts = findProductsExceptTheFirstSubscriptionDc(productRepository, optionProducts);

    return new ProductFactory(productId)
        .basicBenefitProductFactories(
            googleOneAllProductFactory(discountPolicyRepository),
            amazonFreeDeliveryProductFactory(discountPolicyRepository)
        )
        .availableOptionProducts(optionProducts)
        .availableDiscountConditions(
            // 최초가입여부 and !특정 옵션 제외
            and(
                FirstSubscriptionDcCond.with(contractRepository),
                neg(RelatedOptionProductDcCond.with(contractRepository).in(theFirstSubscriptionDcExceptProducts))
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCP0000001"))),
            // 플래티넘, 프라임플러스
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PLATINUM, PRIME_PLUS)
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCM0000001"))),
            // 프라임
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME)
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCM0000002"))),
            // 프라임 and 프로모션기간 내 가입
            and(MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME),
                SubscribedDateRangeDcCond.between(LocalDate.of(2022,9,1), LocalDate.of(2022,12,31))
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCM0000003")))
        );
  }

  @Bean
  public ProductFactory uzooPassLifeProductFactory(ContractRepository contractRepository,
                                                   CustomerServiceProxy customerServiceProxy,
                                                   ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
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
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCP0000001"))),
            // 플래티넘, 프라임플러스
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PLATINUM, PRIME_PLUS)
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCM0000001"))),
            // 프라임
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME)
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCM0000002"))),
            // 프라임 and 프로모션기간 내 가입
            and(MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME),
                SubscribedDateRangeDcCond.between(LocalDate.of(2022,9,1), LocalDate.of(2022,12,31))
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCM0000003")))
        );
  }
 
  @Bean
  public ProductFactory uzooPassMiniProductFactory(ContractRepository contractRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
                                                   ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000003";
  
    return new ProductFactory(productId)
        .basicBenefitProductFactories(
            amazonFreeDeliveryProductFactory(discountPolicyRepository)
        )
        .availableOptionProducts(
            findOptionProducts(productRelationRepository, productId)
        )
        .availableDiscountConditions(
            FirstSubscriptionDcCond.with(contractRepository)
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCP0000002")))
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
  public ProductFactory uzooPassSlimProductFactory(ContractRepository contractRepository,
                                                   DiscountPolicyRepository discountPolicyRepository) {
    String productId = "NMP0000004";
    
    return new ProductFactory(productId)
        .basicBenefitProductFactories(
            amazonFreeDeliveryProductFactory(discountPolicyRepository)
        )
        .availableDiscountConditions(
            FirstSubscriptionDcCond.with(contractRepository)
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCP0000003")))
        );
  }
  
  @Bean
  public ProductFactory googleOneAllProductFactory(DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMB0000001")
        .availableDiscountConditions(
            ContractTypeDcCond.asPackage()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCB0000001")))
        );
  }
  
  
  @Bean
  public ProductFactory amazonFreeDeliveryProductFactory(DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMB0000002")
        .availableDiscountConditions(
            ContractTypeDcCond.asPackage()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCB0000001")))
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
  public ProductFactory baeminProductFactory(ContractRepository contractRepository,
                                             DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000001")
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000003"))),
            and(ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000009")))
        );
  }
  
  @Bean
  public ProductFactory goobneProductFactory(DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000002")
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000001")))
        );
  }
  
  @Bean
  public ProductFactory floAndDataProductFactory(ContractRepository contractRepository,
                                                 CustomerServiceProxy customerServiceProxy,
                                                 DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000004")
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000001"))),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000002"))),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PLATINUM, MAX, PRIME_PLUS)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000004"))),
            // 프라임, 스페셜 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME, SPECIAL)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000005")))
        );
  }
  
  @Bean
  public ProductFactory floAndDataPlusProductFactory(ContractRepository contractRepository,
                                                     CustomerServiceProxy customerServiceProxy,
                                                     DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000008")
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000001"))),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000003"))),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PLATINUM)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000006"))),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME_PLUS, MAX)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000007"))),
            // 프라임, 스페셜 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(PRIME, SPECIAL)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000008")))
        );
  }
  
  @Bean
  public ProductFactory gamepassUltimateProductFactory(ContractRepository contractRepository,
                                                       DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000005")
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000002"))),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000001")))
        );
  }
  
  
  @Bean
  public ProductFactory googleOneMiniProductFactory(DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000006")
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000001")))
        );
  }
  
  @Bean
  public ProductFactory yanoljaProductFactory(DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000010")
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000004")))
        );
  }
  
  @Bean
  public ProductFactoryMap productFactoryMap(ContractRepository contractRepository,
                                             CustomerServiceProxy customerServiceProxy,
                                             ProductRepository productRepository,
                                             DiscountPolicyRepository discountPolicyRepository,
                                             ProductRelationRepository productRelationRepository) {
    ProductFactoryMap productFactoryMap = new ProductFactoryMap();

    // cache화
    discountPolicyRepository.findAll();

    // package
    productFactoryMap.put(uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(uzooPassLifeProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(uzooPassMiniProductFactory(contractRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(uzooPassSlimProductFactory(contractRepository, discountPolicyRepository));
    
    // 기본혜택
    productFactoryMap.put(googleOneAllProductFactory(discountPolicyRepository));
    productFactoryMap.put(amazonFreeDeliveryProductFactory(discountPolicyRepository));
    productFactoryMap.put(sevenElevenProductFactory());
    productFactoryMap.put(twosomePlaceProductFactory());
    
    // 옵션 or 단품
    productFactoryMap.put(baeminProductFactory(contractRepository, discountPolicyRepository));
    productFactoryMap.put(goobneProductFactory(discountPolicyRepository));
    productFactoryMap.put(floAndDataProductFactory(contractRepository, customerServiceProxy, discountPolicyRepository));
    productFactoryMap.put(floAndDataPlusProductFactory(contractRepository, customerServiceProxy, discountPolicyRepository));
    productFactoryMap.put(gamepassUltimateProductFactory(contractRepository, discountPolicyRepository));
    productFactoryMap.put(googleOneMiniProductFactory(discountPolicyRepository));
    productFactoryMap.put(yanoljaProductFactory(discountPolicyRepository));
    
    return productFactoryMap;
  }
}
