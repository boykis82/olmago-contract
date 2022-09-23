package team.caltech.olmago.contract.product.factory;

import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.discount.condition.detail.*;
import team.caltech.olmago.contract.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.plm.product.Product;
import team.caltech.olmago.contract.plm.product.ProductRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static team.caltech.olmago.contract.customer.MobilePhonePricePlan.*;
import static team.caltech.olmago.contract.discount.condition.DiscountCondition.and;
import static team.caltech.olmago.contract.discount.condition.DiscountCondition.neg;
import static team.caltech.olmago.contract.product.factory.ProductRelation.ProductRelationType.PACKAGE_AND_OPTION;

public class AllProductsFactory {
  public static ProductFactory uzooPassAllProductFactory(ContractRepository contractRepository,
                                                  CustomerServiceProxy customerServiceProxy,
                                                  ProductRepository productRepository,
                                                  DiscountPolicyRepository discountPolicyRepository,
                                                  ProductRelationRepository productRelationRepository
  ) {
    String productId = "NMP0000001";
    List<String> optionProducts = findOptionProducts(productRelationRepository, productId);
    List<String> theFirstSubscriptionDcExceptProducts = findProductsExceptTheFirstSubscriptionDc(productRepository, optionProducts);
    
    return new ProductFactory(productId, productRepository, discountPolicyRepository)
        .basicBenefitProductFactories(
            googleOneAllProductFactory(productRepository, discountPolicyRepository),
            amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository)
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
  
  public static ProductFactory uzooPassLifeProductFactory(ContractRepository contractRepository,
                                                   CustomerServiceProxy customerServiceProxy,
                                                   ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
                                                   ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000002";
    List<String> optionProducts = findOptionProducts(productRelationRepository, productId);
    List<String> theFirstSubscriptionDcExceptProducts = findProductsExceptTheFirstSubscriptionDc(productRepository, optionProducts);
    
    return new ProductFactory(productId, productRepository, discountPolicyRepository)
        .basicBenefitProductFactories(
            sevenElevenProductFactory(productRepository, discountPolicyRepository),
            twosomePlaceProductFactory(productRepository, discountPolicyRepository)
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
  
  public static ProductFactory uzooPassMiniProductFactory(ContractRepository contractRepository,
                                                          ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
                                                   ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000003";
    
    return new ProductFactory(productId, productRepository, discountPolicyRepository)
        .basicBenefitProductFactories(
            amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository)
        )
        .availableOptionProducts(
            findOptionProducts(productRelationRepository, productId)
        )
        .availableDiscountConditions(
            FirstSubscriptionDcCond.with(contractRepository)
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCP0000002")))
        );
  }
  
  public static ProductFactory uzooPassSlimProductFactory(ContractRepository contractRepository,
                                                   ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository) {
    String productId = "NMP0000004";
    
    return new ProductFactory(productId, productRepository, discountPolicyRepository)
        .basicBenefitProductFactories(
            amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository)
        )
        .availableDiscountConditions(
            FirstSubscriptionDcCond.with(contractRepository)
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCP0000003")))
        );
  }
  
  public static ProductFactory googleOneAllProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMB0000001", productRepository, discountPolicyRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asPackage()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCB0000001")))
        );
  }

  public static ProductFactory amazonFreeDeliveryProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMB0000002", productRepository, discountPolicyRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asPackage()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCB0000001")))
        );
  }
  
  public static ProductFactory sevenElevenProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMB0000003", productRepository, discountPolicyRepository);
  }
  
  public static ProductFactory twosomePlaceProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMB0000004", productRepository, discountPolicyRepository);
  }
  
  public static ProductFactory baeminProductFactory(ContractRepository contractRepository,
                                             ProductRepository productRepository,
                                             DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000001", productRepository, discountPolicyRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000003"))),
            and(ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(discountPolicyRepository.findAllById(List.of("DCU0000009")))
        );
  }

  public static ProductFactory goobneProductFactory(ProductRepository productRepository,DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000002", productRepository, discountPolicyRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000001")))
        );
  }

  public static ProductFactory floAndDataProductFactory(ContractRepository contractRepository,
                                                 CustomerServiceProxy customerServiceProxy,
                                                 ProductRepository productRepository,
                                                 DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000004", productRepository, discountPolicyRepository)
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

  public static ProductFactory floAndDataPlusProductFactory(ContractRepository contractRepository,
                                                     CustomerServiceProxy customerServiceProxy,
                                                     ProductRepository productRepository,
                                                     DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000008", productRepository, discountPolicyRepository)
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

  public static ProductFactory gamepassUltimateProductFactory(ContractRepository contractRepository,
                                                       ProductRepository productRepository,
                                                       DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000005", productRepository, discountPolicyRepository)
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

  public static ProductFactory googleOneMiniProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000006", productRepository, discountPolicyRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000001")))
        );
  }
  
  public static ProductFactory yanoljaProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return new ProductFactory("NMO0000010", productRepository, discountPolicyRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(discountPolicyRepository.findAllById(List.of("DCO0000004")))
        );
  }
  
  private static List<String> findOptionProducts(ProductRelationRepository productRelationRepository, String productId) {
    return productRelationRepository.findByMainProductAndProductRelationType(productId, PACKAGE_AND_OPTION, LocalDate.now());
  }
  
  private static List<String> findProductsExceptTheFirstSubscriptionDc(ProductRepository productRepository, List<String> optionProducts) {
    return productRepository.findAllById(optionProducts).stream()
        .filter(p -> !p.isTheFirstSubscriptionDcTarget())
        .map(Product::getProductCode)
        .collect(Collectors.toList());
  }
  
}
