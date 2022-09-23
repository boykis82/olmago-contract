package team.caltech.olmago.contract.domain.product.factory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.discount.DiscountSubscription;
import team.caltech.olmago.contract.domain.discount.condition.DiscountCondition;
import team.caltech.olmago.contract.domain.exception.InvalidArgumentException;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.plm.discount.DiscountType;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.product.ProductSubscription;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Accessors(fluent = true, chain = false)
@Getter
@RequiredArgsConstructor
public class ProductFactory {
  private final String productCode;

  private final ProductRepository productRepository;
  private final DiscountPolicyRepository discountPolicyRepository;
  
  private List<DiscountCondition> availableDiscountConditions = new ArrayList<>();
  private List<ProductFactory> basicBenefitProductFactories = new ArrayList<>();
  private List<String> availableOptionProducts = new ArrayList<>();
  
  public ProductFactory availableDiscountConditions(DiscountCondition ...availableDiscountConditions) {
    this.availableDiscountConditions = Arrays.asList(availableDiscountConditions);
    return this;
  }
  
  public ProductFactory basicBenefitProductFactories(ProductFactory ...basicBenefitProductFactories) {
    this.basicBenefitProductFactories = Arrays.asList(basicBenefitProductFactories);
    return this;
  }

  public ProductFactory availableOptionProducts(List<String> optionProducts) {
    this.availableOptionProducts = optionProducts;
    return this;
  }

  public List<String> getBasicBenefitProductCodes() {
    return basicBenefitProductFactories.stream()
        .map(ProductFactory::productCode)
        .collect(Collectors.toList());
  }
  
  public List<ProductSubscription> receiveSubscription(
      Contract contract,
      LocalDateTime subRcvDtm
  ) {
    return receiveSubscription(contract, subRcvDtm, Collections.emptyList());
  }
  
  public List<ProductSubscription> receiveSubscription(
      Contract contract,
      LocalDateTime subRcvDtm,
      List<String> exceptBasicBenefitProductCodes
  ) {
    return Stream.concat(
        Stream.of(receiveMySubscription(contract, subRcvDtm)),
        basicBenefitProductFactories.stream()
            .filter(bbpf -> exceptBasicBenefitProductCodes.stream()
                .noneMatch(pc -> pc.equals(bbpf.productCode()))
            )
            .map(bbpf -> bbpf.receiveSubscription(contract, subRcvDtm))
            .flatMap(List::stream)
    ).collect(Collectors.toList());
  }
  
  private ProductSubscription receiveMySubscription(
      Contract contract,
      LocalDateTime subRcvDtm
  ) {
    Product product = productRepository.findById(productCode)
        .orElseThrow(InvalidArgumentException::new);
    
    contract.validateAvailableProductType(product);
    
    List<DiscountPolicy> satisfiedDCPolicies = satisfiedDiscountPolicies(contract);
    List<DiscountSubscription> dcSubs = subscribeDiscounts(satisfiedDCPolicies, subRcvDtm);
    
    return new ProductSubscription(
        contract,
        product,
        subRcvDtm
    ).discountSubscriptions(dcSubs);
  }

  private List<DiscountPolicy> satisfiedDiscountPolicies(Contract contract) {
    return availableDiscountConditions.stream()
        .filter(dcCond -> dcCond.satisfied(contract))
        .map(DiscountCondition::discountPoliciess)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public List<DiscountPolicy> satisfiedDiscountPolicies(Contract contract, DiscountType discountType) {
    return satisfiedDiscountPolicies(contract).stream()
        .filter(dp -> dp.getDcType().equals(discountType))
        .collect(Collectors.toList());
  }

  private List<DiscountSubscription> subscribeDiscounts(
      List<DiscountPolicy> discountPolicies,
      LocalDateTime subRcvDtm
  ) {
    return discountPolicies.stream()
        .map(discountPolicy -> subscribeDiscount(discountPolicy, subRcvDtm))
        .collect(Collectors.toList());
  }
  
  private DiscountSubscription subscribeDiscount(
      DiscountPolicy discountPolicy,
      LocalDateTime subRcvDtm
  ) {
    return DiscountSubscription.builder()
        .discountPolicy(discountPolicy)
        .subRcvDtm(subRcvDtm)
        .build();
  }

  public boolean isAvailableOptionProduct(String optionProduct) {
    return availableOptionProducts.stream()
        .anyMatch(op -> op.equals(optionProduct));
  }

  public List<String> getShouldBeTerminatedProductCodes(ProductFactory afterProductFactory) {
    return getBasicBenefitProductCodes().stream()
        .filter(bfp -> afterProductFactory.getBasicBenefitProductCodes().stream().noneMatch(bfp::equals))
        .collect(Collectors.toList());
  }
}
