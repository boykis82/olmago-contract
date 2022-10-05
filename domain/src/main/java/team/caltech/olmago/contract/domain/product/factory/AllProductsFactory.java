package team.caltech.olmago.contract.domain.product.factory;

import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.customer.MobilePhonePricePlan;
import team.caltech.olmago.contract.domain.discount.condition.detail.*;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;

import java.time.LocalDate;
import java.util.List;

import static team.caltech.olmago.contract.domain.discount.condition.DiscountCondition.and;

public class AllProductsFactory {
  private final static LocalDate MOBILE_PHONE_PROM_START_DT = LocalDate.of(2022,9,1);
  private final static LocalDate MOBILE_PHONE_PROM_END_DT = LocalDate.of(2022,12,31);
  
  public static ProductFactory uzooPassAllProductFactory(ContractRepository contractRepository,
                                                         CustomerServiceProxy customerServiceProxy,
                                                         ProductRepository productRepository,
                                                         DiscountPolicyRepository discountPolicyRepository,
                                                         ProductRelationRepository productRelationRepository
  ) {
    String productId = "NMP0000001";

    return new ProductFactory(productId, productRepository, discountPolicyRepository, productRelationRepository)
        .basicBenefitProductFactories(
            googleOneAllProductFactory(productRepository, discountPolicyRepository, productRelationRepository),
            amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository, productRelationRepository)
        )
        .availableDiscountConditions(
            // 최초가입여부 and 최초가입할인 가능 옵션
            and(
                FirstSubscriptionDcCond.with(contractRepository),
                FirstSubscriptionDcAvailableOptionProductCond.with(contractRepository, productRepository)
            ).discountPolicies(List.of("DCP0000001")),
            // 플래티넘, 프라임플러스
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PLATINUM, MobilePhonePricePlan.PRIME_PLUS)
                .discountPolicies(List.of("DCM0000001")),
            // 프라임
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PRIME)
                .discountPolicies(List.of("DCM0000002")),
            // 프라임 and 프로모션기간 내 가입
            and(MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PRIME),
                SubscribedDateRangeDcCond.between(MOBILE_PHONE_PROM_START_DT, MOBILE_PHONE_PROM_END_DT)
            ).discountPolicies(List.of("DCM0000003"))
        );
  }
  
  public static ProductFactory uzooPassLifeProductFactory(ContractRepository contractRepository,
                                                          CustomerServiceProxy customerServiceProxy,
                                                          ProductRepository productRepository,
                                                          DiscountPolicyRepository discountPolicyRepository,
                                                          ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000002";
    return new ProductFactory(productId, productRepository, discountPolicyRepository, productRelationRepository)
        .basicBenefitProductFactories(
            sevenElevenProductFactory(productRepository, discountPolicyRepository, productRelationRepository),
            twosomePlaceProductFactory(productRepository, discountPolicyRepository, productRelationRepository)
        )
        .availableDiscountConditions(
            // 최초가입여부 and !특정 옵션 제외
            and(
                FirstSubscriptionDcCond.with(contractRepository),
                FirstSubscriptionDcAvailableOptionProductCond.with(contractRepository, productRepository)
            ).discountPolicies(List.of("DCP0000001")),
            // 플래티넘, 프라임플러스
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PLATINUM, MobilePhonePricePlan.PRIME_PLUS)
                .discountPolicies(List.of("DCM0000001")),
            // 프라임
            MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PRIME)
                .discountPolicies(List.of("DCM0000002")),
            // 프라임 and 프로모션기간 내 가입
            and(MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PRIME),
                SubscribedDateRangeDcCond.between(MOBILE_PHONE_PROM_START_DT, MOBILE_PHONE_PROM_END_DT)
            ).discountPolicies(List.of("DCM0000003"))
        );
  }
  
  public static ProductFactory uzooPassMiniProductFactory(ContractRepository contractRepository,
                                                          ProductRepository productRepository,
                                                          DiscountPolicyRepository discountPolicyRepository,
                                                          ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000003";
    return new ProductFactory(productId, productRepository, discountPolicyRepository, productRelationRepository)
        .basicBenefitProductFactories(
            amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository, productRelationRepository)
        )
        .availableDiscountConditions(
            FirstSubscriptionDcCond.with(contractRepository)
                .discountPolicies(List.of("DCP0000002"))
        );
  }
  
  public static ProductFactory uzooPassSlimProductFactory(ContractRepository contractRepository,
                                                          ProductRepository productRepository,
                                                          DiscountPolicyRepository discountPolicyRepository,
                                                          ProductRelationRepository productRelationRepository) {
    String productId = "NMP0000004";
    return new ProductFactory(productId, productRepository, discountPolicyRepository, productRelationRepository)
        .basicBenefitProductFactories(
            amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository, productRelationRepository)
        )
        .availableDiscountConditions(
            FirstSubscriptionDcCond.with(contractRepository)
                .discountPolicies(List.of("DCP0000003"))
        );
  }
  
  public static ProductFactory googleOneAllProductFactory(ProductRepository productRepository,
                                                          DiscountPolicyRepository discountPolicyRepository,
                                                          ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMB0000001", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asPackage()
                .discountPolicies(List.of("DCB0000001"))
        );
  }

  public static ProductFactory amazonFreeDeliveryProductFactory(ProductRepository productRepository,
                                                                DiscountPolicyRepository discountPolicyRepository,
                                                                ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMB0000002", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asPackage()
                .discountPolicies(List.of("DCB0000001"))
        );
  }
  
  public static ProductFactory sevenElevenProductFactory(ProductRepository productRepository,
                                                         DiscountPolicyRepository discountPolicyRepository,
                                                         ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMB0000003", productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  public static ProductFactory twosomePlaceProductFactory(ProductRepository productRepository,
                                                          DiscountPolicyRepository discountPolicyRepository,
                                                          ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMB0000004", productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  public static ProductFactory baeminProductFactory(ContractRepository contractRepository,
                                                    ProductRepository productRepository,
                                                    DiscountPolicyRepository discountPolicyRepository,
                                                    ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMO0000001", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(List.of("DCO0000003")),
            and(ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(List.of("DCU0000009"))
        );
  }

  public static ProductFactory goobneProductFactory(ProductRepository productRepository,
                                                    DiscountPolicyRepository discountPolicyRepository,
                                                    ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMO0000002", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(List.of("DCO0000001"))
        );
  }

  public static ProductFactory floAndDataProductFactory(ContractRepository contractRepository,
                                                        CustomerServiceProxy customerServiceProxy,
                                                        ProductRepository productRepository,
                                                        DiscountPolicyRepository discountPolicyRepository,
                                                        ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMO0000004", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption()
                .discountPolicies(List.of("DCO0000001")),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(List.of("DCU0000002")),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PLATINUM, MobilePhonePricePlan.MAX, MobilePhonePricePlan.PRIME_PLUS)
            ).discountPolicies(List.of("DCU0000004")),
            // 프라임, 스페셜 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PRIME, MobilePhonePricePlan.SPECIAL)
            ).discountPolicies(List.of("DCU0000005"))
        );
  }

  public static ProductFactory floAndDataPlusProductFactory(ContractRepository contractRepository,
                                                            CustomerServiceProxy customerServiceProxy,
                                                            ProductRepository productRepository,
                                                            DiscountPolicyRepository discountPolicyRepository,
                                                            ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMO0000008", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption()
                .discountPolicies(List.of("DCO0000001")),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(List.of("DCU0000003")),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PLATINUM)
            ).discountPolicies(List.of("DCU0000006")),
            // 단품 & 플래티넘, 프라임플러스, 맥스 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PRIME_PLUS, MobilePhonePricePlan.MAX)
            ).discountPolicies(List.of("DCU0000007")),
            // 프라임, 스페셜 요금제
            and(
                ContractTypeDcCond.asUnit(),
                MobilePhonePricePlanDcCond.with(customerServiceProxy).in(MobilePhonePricePlan.PRIME, MobilePhonePricePlan.SPECIAL)
            ).discountPolicies(List.of("DCU0000008"))
        );
  }

  public static ProductFactory gamepassUltimateProductFactory(ContractRepository contractRepository,
                                                              ProductRepository productRepository,
                                                              DiscountPolicyRepository discountPolicyRepository,
                                                              ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMO0000005", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            // 옵션
            ContractTypeDcCond.asOption()
                .discountPolicies(List.of("DCO0000002")),
            // 단품 & 최초가입
            and(
                ContractTypeDcCond.asUnit(),
                FirstSubscriptionDcCond.with(contractRepository)
            ).discountPolicies(List.of("DCU0000001"))
        );
  }

  public static ProductFactory googleOneMiniProductFactory(ProductRepository productRepository,
                                                           DiscountPolicyRepository discountPolicyRepository,
                                                           ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMO0000006", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(List.of("DCO0000001"))
        );
  }
  
  public static ProductFactory yanoljaProductFactory(ProductRepository productRepository,
                                                     DiscountPolicyRepository discountPolicyRepository,
                                                     ProductRelationRepository productRelationRepository) {
    return new ProductFactory("NMO0000010", productRepository, discountPolicyRepository, productRelationRepository)
        .availableDiscountConditions(
            ContractTypeDcCond.asOption()
                .discountPolicies(List.of("DCO0000004"))
        );
  }
}
