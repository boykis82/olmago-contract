package team.caltech.olmago.contract.domain.contract;

import team.caltech.olmago.contract.domain.discount.DiscountSubscription;
import team.caltech.olmago.contract.domain.plm.PlmFixtures;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicy;
import team.caltech.olmago.contract.domain.plm.product.Product;
import team.caltech.olmago.contract.domain.product.ProductSubscription;

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
  
  public static Contract createUzoopassAllContract(long customerId, long orderId, LocalDateTime subRcvDtm, boolean withProdSub) {
    Contract c =  Contract.builder()
        .customerId(customerId)
        .orderId(orderId)
        .contractType(ContractType.PACKAGE)
        .feeProductCode("NMP0000001")
        .subRcvDtm(subRcvDtm)
        .build();
    
    if (withProdSub)
      createUzoopassProductSubscriptions(c);
    return c;
  }


  public static List<ProductSubscription> createUzoopassProductSubscriptions(Contract contract) {
    List<ProductSubscription> ps = List.of(
        createProductSubscription(contract, "NMP0000001", "DCP0000001", "DCM0000001"),
        createProductSubscription(contract, "NMB0000001", "DCB0000001"),
        createProductSubscription(contract, "NMB0000002", "DCB0000001")
    );
    contract.addProductSubscriptions(ps);
    return ps;
  }
  
  public static Contract createBaeminContract(long customerId, long orderId, LocalDateTime subRcvDtm, ContractType contractType, boolean withProdSub) {
    String productCode = "NMO0000001";
    Contract c = Contract.builder()
        .customerId(customerId)
        .orderId(orderId)
        .contractType(contractType)
        .feeProductCode(productCode)
        .subRcvDtm(subRcvDtm)
        .build();
    
    if (withProdSub) {
      c.addProductSubscriptions(List.of(
          contractType == ContractType.OPTION
              ? createProductSubscription(c, productCode, "DCO0000003")
              : createProductSubscription(c, productCode)
      ));
    }
    return c;
  }

  public static Contract createYanoljaContract(long customerId, long orderId, LocalDateTime subRcvDtm, ContractType contractType, boolean withProdSub) {
    String productCode = "NMO0000010";
    Contract c = Contract.builder()
        .customerId(customerId)
        .orderId(orderId)
        .contractType(contractType)
        .feeProductCode(productCode)
        .subRcvDtm(subRcvDtm)
        .build();
    
    if (withProdSub) {
      c.addProductSubscriptions(List.of(
          contractType == ContractType.OPTION
              ? createProductSubscription(c, productCode, "DCO0000004")
              : createProductSubscription(c, productCode)
      ));
    }
    return c;
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
