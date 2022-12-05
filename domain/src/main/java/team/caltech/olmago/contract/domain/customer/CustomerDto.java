package team.caltech.olmago.contract.domain.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Builder
public class CustomerDto {
  private long customerId;
  private String name;
  
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthday;
  private String ci;
  
  private Long svcMgmtNum;
  private String productName;
  private String mobilePhonePricePlan;
  
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
  private LocalDateTime linkedDateTime;
  private List<String> dcTargetUzooPassProductCodes;
  
  @Builder
  CustomerDto(long customerId, String name, LocalDate birthday, String ci, Long svcMgmtNum, String productName, String mobilePhonePricePlan, LocalDateTime linkedDateTime, List<String> dcTargetUzooPassProductCodes) {
    this.customerId = customerId;
    this.name = name;
    this.birthday = birthday;
    this.ci = ci;
    this.svcMgmtNum = svcMgmtNum;
    this.productName = productName;
    this.mobilePhonePricePlan = mobilePhonePricePlan;
    this.linkedDateTime = linkedDateTime;
    this.dcTargetUzooPassProductCodes = dcTargetUzooPassProductCodes;
  }
  
  public boolean matchMobilePhonePricePlanAndUzooPassProductCode(
      MobilePhonePricePlan mobilePhonePricePlan,
      String uzooPassProductCode
  ) {
    return mobilePhonePricePlan.equals(MobilePhonePricePlan.valueOf(this.mobilePhonePricePlan))
        && dcTargetUzooPassProductCodes.stream().anyMatch(pc -> pc.equals(uzooPassProductCode));
  }
  
}


