package team.caltech.olmago.contract.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.contract.*;
import team.caltech.olmago.contract.discount.DiscountSubscription;
import team.caltech.olmago.contract.plm.discount.DiscountUnit;
import team.caltech.olmago.contract.product.ProductSubscription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ContractDto {
  @Getter
  static class ProductSubscriptionDto {
    @Getter
    static class DiscountSubscriptionDto {
      private long discountSubscriptionId;
    
      private String dcPolicyCode;
      private String dcPolicyName;
      private DiscountUnit dcUnit;
      private int dcAmountOrRate;
    
      private LocalDateTime subscriptionReceivedDateTime;
      private LocalDateTime subscriptionCompletedDateTime;
      private LocalDateTime terminationReceivedDateTime;
      private LocalDateTime cancelTerminationReceiptDateTime;
      private LocalDateTime terminationCompletedDateTime;
    
      private LocalDate discountStartDate;
      private LocalDate discountEndDate;
      private LocalDate discountRegisterDate;
      private LocalDate discountEndRegisterDate;
    
      static DiscountSubscriptionDto of(DiscountSubscription e) {
        DiscountSubscriptionDto dto = new DiscountSubscriptionDto();
        dto.discountSubscriptionId = e.getId();
        dto.dcPolicyCode = e.getDiscountPolicy().getDcPolicyCode();
        dto.dcPolicyName = e.getDiscountPolicy().getDcPolicyName();
        dto.dcUnit = e.getDiscountPolicy().getDcUnit();
        dto.dcAmountOrRate = e.getDiscountPolicy().getDcAmountOrRate();
        dto.subscriptionReceivedDateTime = e.getLifeCycle().getSubscriptionReceivedDateTime();
        dto.subscriptionCompletedDateTime = e.getLifeCycle().getSubscriptionCompletedDateTime();
        dto.terminationReceivedDateTime = e.getLifeCycle().getTerminationReceivedDateTime();
        dto.cancelTerminationReceiptDateTime = e.getLifeCycle().getCancelTerminationReceiptDateTime();
        dto.terminationCompletedDateTime = e.getLifeCycle().getTerminationCompletedDateTime();
        dto.discountStartDate = e.getDiscountStartDate();
        dto.discountEndDate = e.getDiscountEndDate();
        dto.discountRegisterDate = e.getDiscountRegisterDate();
        dto.discountEndRegisterDate = e.getDiscountEndRegisterDate();
        return dto;
      }
    }
  
    private long productSubscriptionId;
    private String productCode;
    private String productName;
    private int feeVatIncluded;
    private LocalDateTime subscriptionReceivedDateTime;
    private LocalDateTime subscriptionCompletedDateTime;
    private LocalDateTime terminationReceivedDateTime;
    private LocalDateTime cancelTerminationReceiptDateTime;
    private LocalDateTime terminationCompletedDateTime;
    private List<DiscountSubscriptionDto> discountSubscriptions;
    
    static ProductSubscriptionDto of(ProductSubscription ps) {
      ProductSubscriptionDto dto =  new ProductSubscriptionDto();
      dto.productSubscriptionId = ps.getId();
      dto.productCode = ps.getProductCode();
      dto.productName = ps.getProduct().getProductName();
      dto.feeVatIncluded = ps.getProduct().getFeeVatIncluded();
      dto.subscriptionReceivedDateTime = ps.getLifeCycle().getSubscriptionReceivedDateTime();
      dto.subscriptionCompletedDateTime = ps.getLifeCycle().getSubscriptionCompletedDateTime();
      dto.terminationReceivedDateTime = ps.getLifeCycle().getTerminationReceivedDateTime();
      dto.cancelTerminationReceiptDateTime = ps.getLifeCycle().getCancelTerminationReceiptDateTime();
      dto.terminationCompletedDateTime = ps.getLifeCycle().getTerminationCompletedDateTime();
      dto.discountSubscriptions = ps.getDiscountSubscriptions().stream()
          .map(DiscountSubscriptionDto::of)
          .collect(Collectors.toList());
      return dto;
    }
  }
  
  private long contractId;
  private long customerId;
  private long lastOrderId;
  private ContractType contractType;
  private LocalDateTime subscriptionReceivedDateTime;
  private LocalDateTime subscriptionCompletedDateTime;
  private LocalDateTime terminationReceivedDateTime;
  private LocalDateTime cancelTerminationReceiptDateTime;
  private LocalDateTime terminationCompletedDateTime;
  private LocalDate currentBillStartDate;
  private LocalDate currentBillEndDate;
  private LocalDateTime lastRegularPaymentCompletedDateTime;
  private String feeProductCode;
  private List<ProductSubscriptionDto> productSubscriptions;
  private long packageId;
  
  public static ContractDto of(Contract contract) {
    ContractDto dto =  new ContractDto();
    dto.contractId = contract.getId();
    dto.customerId = contract.getCustomerId();
    dto.lastOrderId = contract.getLastOrderId();
    dto.contractType = contract.getContractType();
    dto.subscriptionReceivedDateTime = contract.getLifeCycle().getSubscriptionReceivedDateTime();
    dto.subscriptionCompletedDateTime = contract.getLifeCycle().getSubscriptionCompletedDateTime();
    dto.terminationReceivedDateTime = contract.getLifeCycle().getTerminationReceivedDateTime();
    dto.cancelTerminationReceiptDateTime = contract.getLifeCycle().getCancelTerminationReceiptDateTime();
    dto.terminationCompletedDateTime = contract.getLifeCycle().getTerminationCompletedDateTime();
    dto.currentBillStartDate = contract.getBillCycle().getCurrentBillStartDate();
    dto.currentBillEndDate = contract.getBillCycle().getCurrentBillEndDate();
    dto.lastRegularPaymentCompletedDateTime = contract.getLastPaymentDtm();
    dto.feeProductCode = contract.getFeeProductCode();
    dto.packageId = contract.getPackageId();
    dto.productSubscriptions = contract.getProductSubscriptions().stream()
        .map(ProductSubscriptionDto::of)
        .collect(Collectors.toList());
    
    return dto;
  }
}
