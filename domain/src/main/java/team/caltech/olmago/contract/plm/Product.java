package team.caltech.olmago.contract.plm;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Product {
  @Id
  @Column(length = 10)
  private String productCode;
  
  @Column(length = 80)
  private String productName;
  
  @Enumerated(EnumType.STRING)
  private BillPeriod billPeriod;
  
  @Enumerated(EnumType.STRING)
  private AvailableProductType availableProductType;
  
  private int feeVatIncluded;
  
  private boolean isTheFirstSubscriptionDcTarget;
  
  @Builder
  public Product(String productCode,
                 String productName,
                 BillPeriod billPeriod,
                 AvailableProductType availableProductType,
                 int feeVatIncluded,
                 boolean isTheFirstSubscriptionDcTarget) {
    this.productCode = productCode;
    this.productName = productName;
    this.billPeriod = billPeriod;
    this.availableProductType = availableProductType;
    this.feeVatIncluded = feeVatIncluded;
    this.isTheFirstSubscriptionDcTarget = isTheFirstSubscriptionDcTarget;
  }
}
