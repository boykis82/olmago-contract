package team.caltech.olmago.contract.domain.plm.discount;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "discount_policy")
public class DiscountPolicy {
  @Id
  private String dcPolicyCode;
  
  @Column(nullable = false)
  private String dcPolicyName;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DiscountUnit dcUnit;
  
  @Column(nullable = false)
  private int dcAmountOrRate;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DiscountType dcType;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DiscountStartDateType dcStartDateType;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DiscountPeriodType dcPeriodType;
  
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
