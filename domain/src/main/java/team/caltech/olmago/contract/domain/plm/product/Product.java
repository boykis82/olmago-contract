package team.caltech.olmago.contract.domain.plm.product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "prod")
public class Product {
  @Id
  @Column(name = "prod_cd", length = 10, nullable = false)
  private String productCode;
  
  @Column(name = "prod_nm", length = 80, nullable = false)
  private String productName;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "bill_prd", length = 20, nullable = false)
  private BillPeriod billPeriod;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "avail_prod_typ", length = 40, nullable = false)
  private AvailableProductType availableProductType;
  
  @Column(name = "fee_vat_incl", nullable = false)
  private int feeVatIncluded;
  
  @Column(name = "fst_sub_dc_tgt", length = 1, nullable = false)
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
