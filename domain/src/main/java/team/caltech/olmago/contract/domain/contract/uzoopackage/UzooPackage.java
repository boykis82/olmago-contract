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
@Table(name = "package", indexes = {
    @Index(name = "package_n1", columnList = "pkg_cntrct_id, opt_cntrct_id"),
    @Index(name = "package_n2", columnList = "opt_cntrct_id, pkg_cntrct_id"),
    @Index(name = "package_n3", columnList = "last_ord_id"),
})
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
  
  @Column(name = "last_ord_id")
  private Long lastOrderId;

  @Builder
  public UzooPackage(Contract packageContract,
                     Contract optionContract,
                     Long lastOrderId,
                     LocalDateTime subscriptionReceivedDateTime) {
    this.packageContract = packageContract;
    this.optionContract = optionContract;
    this.lastOrderId = lastOrderId;
    this.lifeCycle = new LifeCycle(subscriptionReceivedDateTime);
  }
  
  public void cancelSubscriptionReceipt(LocalDateTime cancelSubscriptionReceiptDateTime) {
    lifeCycle.cancelSubscriptionReceipt(cancelSubscriptionReceiptDateTime);
  }

  public void completeSubscription(LocalDateTime subscriptionCompletedDateTime) {
    lifeCycle.completeSubscription(subscriptionCompletedDateTime);
  }

  public void receiveTermination(LocalDateTime terminationReceivedDateTime, long orderId) {
    lifeCycle.receiveTermination(terminationReceivedDateTime);
    this.lastOrderId = orderId;
  }

  public void cancelTerminationReceipt(LocalDateTime cancelTerminationReceiptDateTime) {
    lifeCycle.cancelTerminationReceipt(cancelTerminationReceiptDateTime);
  }

  public void completeTermination(LocalDateTime terminationCompletedDateTime) {
    lifeCycle.completeTermination(terminationCompletedDateTime);
  }

}
