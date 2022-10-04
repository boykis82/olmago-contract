package team.caltech.olmago.contract.domain.contract.uzoopackage;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.common.LifeCycle;

import javax.persistence.*;
import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Entity
@Table(name = "package")
public class UzooPackage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Version
  private long version;
  
  @ManyToOne
  @JoinColumn(name = "pkg_cntrct_id")
  private Contract packageContract;
  
  @ManyToOne
  @JoinColumn(name = "opt_cntrct_id")
  private Contract optionContract;

  @Embedded
  private LifeCycle lifeCycle;

  @Builder
  public UzooPackage(Contract packageContract,
                     Contract optionContract,
                     LocalDateTime subscriptionReceivedDateTime) {
    this.packageContract = packageContract;
    this.optionContract = optionContract;
    this.lifeCycle = new LifeCycle(subscriptionReceivedDateTime);
  }
  
  public void cancelSubscriptionReceipt(LocalDateTime cancelSubscriptionReceiptDateTime) {
    lifeCycle.cancelSubscriptionReceipt(cancelSubscriptionReceiptDateTime);
  }

  public void completeSubscription(LocalDateTime subscriptionCompletedDateTime) {
    lifeCycle.completeSubscription(subscriptionCompletedDateTime);
  }

  public void receiveTermination(LocalDateTime terminationReceivedDateTime) {
    lifeCycle.receiveTermination(terminationReceivedDateTime);
  }

  public void cancelTerminationReceipt(LocalDateTime cancelTerminationReceiptDateTime) {
    lifeCycle.cancelTerminationReceipt(cancelTerminationReceiptDateTime);
  }

  public void completeTermination(LocalDateTime terminationCompletedDateTime) {
    lifeCycle.completeTermination(terminationCompletedDateTime);
  }

}
