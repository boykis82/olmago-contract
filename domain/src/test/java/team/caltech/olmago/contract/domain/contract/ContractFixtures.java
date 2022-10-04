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
  
  public static Contract createUzoopassAllContract(LocalDateTime subRcvDtm) {
    Contract c =  uzooPassAllContractBuilder(subRcvDtm).build();
    createUzoopassProductSubscriptions(c);
    return c;
  }
  
  public static Contract.ContractBuilder uzooPassAllContractBuilder(LocalDateTime subRcvDtm) {
    return Contract.builder()
        .customerId(1L)
        .orderId(2L)
        .contractType(ContractType.PACKAGE)
        .feeProductCode("NMP0000001")
        .subRcvDtm(subRcvDtm);
  }
  
  public static Contract.ContractBuilder contractBuilder(String feeProductCode, LocalDateTime subRcvDtm, ContractType contractType) {
    return Contract.builder()
        .customerId(1L)
        .orderId(2L)
        .contractType(contractType)
        .feeProductCode(feeProductCode)
        .subRcvDtm(subRcvDtm);
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
  
  public static Contract createBaeminContract(LocalDateTime subRcvDtm, ContractType contractType) {
    String productCode = "NMO0000001";
    Contract c = contractBuilder(productCode, subRcvDtm, contractType).build();
    c.addProductSubscriptions(List.of(
        contractType == ContractType.OPTION
            ? createProductSubscription(c, productCode, "DCO0000003")
            : createProductSubscription(c, productCode)
    ));
    return c;
  }

  public static Contract createYanoljaContract(LocalDateTime subRcvDtm, ContractType contractType) {
    String productCode = "NMO0000010";
    Contract c = contractBuilder(productCode, subRcvDtm, contractType).build();
    c.addProductSubscriptions(List.of(
        contractType == ContractType.OPTION
            ? createProductSubscription(c, productCode, "DCO0000004")
            : createProductSubscription(c, productCode)
    ));
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
