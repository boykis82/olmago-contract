package team.caltech.olmago.contract.contract;

import team.caltech.olmago.contract.discount.DiscountSubscription;
import team.caltech.olmago.contract.plm.DiscountPolicy;
import team.caltech.olmago.contract.plm.PlmFixtures;
import team.caltech.olmago.contract.plm.Product;
import team.caltech.olmago.contract.product.ProductSubscription;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContractFixtures {
  private static final Map<String, Product> products;
  private static final Map<String, DiscountPolicy> discountPolicies;
  
  static {
    products = PlmFixtures.setupProducts().stream()
        .collect(Collectors.toMap(Product::getProductCode, p -> p));
    discountPolicies = PlmFixtures.setupDiscountPolicies().stream()
        .collect(Collectors.toMap(DiscountPolicy::getDcPolicyCode, d -> d));
  }
  
  public static Contract createUzoopassAllContract(LocalDateTime subRcvDtm) {
    return Contract.builder()
        .id(1L)
        .customerId(1L)
        .orderId(2L)
        .contractType(ContractType.PACKAGE)
        .feeProductCode("NMP0000001")
        .subRcvDtm(subRcvDtm)
        .build();
  }

  public static List<ProductSubscription> createUzoopassProductSubscriptions(Contract contract) {
    return List.of(
        createProductSubscription(contract, "NMP0000001", "DCP0000001", "DCM0000001"),
        createProductSubscription(contract, "NMB0000001", "DCB0000001"),
        createProductSubscription(contract, "NMB0000002", "DCB0000001")
    );
  }
  
  public static Contract createBaeminContract(LocalDateTime subRcvDtm, ContractType contractType) {
    return Contract.builder()
        .id(1L)
        .customerId(2L)
        .orderId(3L)
        .contractType(contractType)
        .feeProductCode("NMO0000001")
        .subRcvDtm(subRcvDtm)
        .build();
  }

  public static Contract createYanoljaContract(LocalDateTime subRcvDtm, ContractType contractType) {
    return Contract.builder()
        .id(3L)
        .customerId(2L)
        .orderId(3L)
        .contractType(contractType)
        .feeProductCode("NMO0000010")
        .subRcvDtm(subRcvDtm)
        .build();
  }

  public static ProductSubscription createProductSubscription(Contract contract, String productCode, String ... dcCodes) {
    return createProductSubscription(contract, productCode, contract.getLifeCycle().getSubscriptionReceivedDateTime(), dcCodes);
  }
  
  public static ProductSubscription createProductSubscription(Contract contract, String productCode, LocalDateTime subRcvDtm, String ... dcCodes) {
    ProductSubscription ps = ProductSubscription.builder()
        .product(products.get(productCode))
        .contract(contract)
        .subscriptionReceivedDateTime(subRcvDtm)
        .build();
    
    ps.discountSubscriptions(
        Arrays.stream(dcCodes)
            .map(dcCode -> DiscountSubscription.builder()
                .discountPolicy(discountPolicies.get(dcCode))
                .productSubscription(ps)
                .subRcvDtm(subRcvDtm)
                .build()
            )
            .collect(Collectors.toList())
    );
    return ps;
  }
}
