package team.caltech.olmago.contract.contract;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Embeddable
public class LifeCycle {
  @Column(nullable = false)
  private LocalDateTime subscriptionReceivedDateTime;
  private LocalDateTime subscriptionCompletedDateTime;
  private LocalDateTime terminationReceivedDateTime;
  private LocalDateTime cancelTerminationReceiptDateTime;
  private LocalDateTime terminationCompletedDateTime;
  
  public LifeCycle(LocalDateTime subscriptionReceivedDateTime) {
    receiveSubscription(subscriptionReceivedDateTime);
  }
  
  public void receiveSubscription(LocalDateTime subscriptionReceivedDateTime) {
    this.subscriptionReceivedDateTime = subscriptionReceivedDateTime;
  }
  
  public void completeSubscription(LocalDateTime subscriptionCompletedDateTime) {
    this.subscriptionCompletedDateTime = subscriptionCompletedDateTime;
  }
  
  public void receiveTermination(LocalDateTime terminationReceivedDateTime) {
    this.terminationReceivedDateTime = terminationReceivedDateTime;
    this.cancelTerminationReceiptDateTime = null;
  }
  
  public void completeTermination(LocalDateTime terminationCompletedDateTime) {
    this.terminationCompletedDateTime = terminationCompletedDateTime;
  }
  
  public void cancelTerminationReceipt(LocalDateTime cancelTerminationReceiptDateTime) {
    this.terminationReceivedDateTime = null;
    this.cancelTerminationReceiptDateTime = cancelTerminationReceiptDateTime;
  }

  public boolean isActive() {
    return terminationReceivedDateTime == null && terminationCompletedDateTime == null;
  }
}
