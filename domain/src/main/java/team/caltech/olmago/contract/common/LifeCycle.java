package team.caltech.olmago.contract.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
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

  public boolean isSubscriptionReceived() {
    return subscriptionReceivedDateTime != null &&
        subscriptionCompletedDateTime == null &&
        terminationReceivedDateTime == null &&
        terminationCompletedDateTime == null;
  }

  public boolean isSubscriptionCompleted() {
    return subscriptionReceivedDateTime != null &&
        subscriptionCompletedDateTime != null &&
        terminationReceivedDateTime == null &&
        terminationCompletedDateTime == null;
  }

  public boolean isTerminationReceived() {
    return subscriptionReceivedDateTime != null &&
        subscriptionCompletedDateTime != null &&
        terminationReceivedDateTime != null &&
        terminationCompletedDateTime == null;
  }

  public boolean isTerminationCompleted() {
    return subscriptionReceivedDateTime != null &&
        subscriptionCompletedDateTime != null &&
        terminationReceivedDateTime != null &&
        terminationCompletedDateTime != null;
  }
}
