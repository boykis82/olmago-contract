package team.caltech.olmago.contract.domain.plm.discount;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "dc_plcy")
public class DiscountPolicy {
  @Id
  @Column(name = "dc_plcy_cd", length = 10, nullable = false)
  private String dcPolicyCode;
  
  @Column(name = "dc_plcy_nm", length = 80, nullable = false)
  private String dcPolicyName;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "dc_unit", length = 10, nullable = false)
  private DiscountUnit dcUnit;
  
  @Column(name = "dc_amt_or_rate", nullable = false)
  private int dcAmountOrRate;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "dc_typ", length = 40, nullable = false)
  private DiscountType dcType;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "dc_sta_dt_typ", length = 20, nullable = false)
  private DiscountStartDateType dcStartDateType;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "dc_prd_typ", length = 20, nullable = false)
  private DiscountPeriodType dcPeriodType;
  
  @Column(name = "copn_plcy_cd", length = 40)
  private String couponPolicyCode;
  
  @Builder
  public DiscountPolicy(String dcPolicyCode,
                        String dcPolicyName,
                        DiscountUnit dcUnit,
                        int dcAmountOrRate,
                        DiscountType dcType,
                        DiscountStartDateType dcStartDateType,
                        DiscountPeriodType dcPeriodType,
                        String couponPolicyCode) {
    this.dcPolicyCode = dcPolicyCode;
    this.dcPolicyName = dcPolicyName;
    this.dcUnit = dcUnit;
    this.dcAmountOrRate = dcAmountOrRate;
    this.dcType = dcType;
    this.dcStartDateType = dcStartDateType;
    this.dcPeriodType = dcPeriodType;
    this.couponPolicyCode = couponPolicyCode;
  }
}
