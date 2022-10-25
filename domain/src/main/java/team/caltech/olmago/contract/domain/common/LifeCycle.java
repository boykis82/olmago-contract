package team.caltech.olmago.contract.domain.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Embeddable
public class LifeCycle {
  @Column(name = "sub_rcv_dtm")
  private LocalDateTime subscriptionReceivedDateTime;
  @Column(name = "sub_rcv_cncl_dtm")
  private LocalDateTime cancelSubscriptionReceiptDateTime;
  @Column(name = "sub_cmpl_dtm")
  private LocalDateTime subscriptionCompletedDateTime;
  @Column(name = "term_rcv_dtm")
  private LocalDateTime terminationReceivedDateTime;
  @Column(name = "term_rcv_cncl_dtm")
  private LocalDateTime cancelTerminationReceiptDateTime;
  @Column(name = "term_cmpl_dtm")
  private LocalDateTime terminationCompletedDateTime;
  
  public LifeCycle(LocalDateTime subscriptionReceivedDateTime) {
    receiveSubscription(subscriptionReceivedDateTime);
  }
  
  public void receiveSubscription(LocalDateTime subscriptionReceivedDateTime) {
    if (this.subscriptionReceivedDateTime != null ||
        subscriptionCompletedDateTime != null ||
        terminationReceivedDateTime != null ||
        terminationCompletedDateTime != null
    ) {
      throw new IllegalStateException();
    }
    this.subscriptionReceivedDateTime = subscriptionReceivedDateTime;
  }
  
  public void cancelSubscriptionReceipt(LocalDateTime cancelSubscriptionReceiptDateTime) {
    if (!isSubscriptionReceived()) {
      throw new IllegalStateException();
    }
    this.subscriptionReceivedDateTime = null;
    this.cancelSubscriptionReceiptDateTime = cancelSubscriptionReceiptDateTime;
  }
  
  public void completeSubscription(LocalDateTime subscriptionCompletedDateTime) {
    if (!isSubscriptionReceived()) {
      throw new IllegalStateException();
    }
    this.subscriptionCompletedDateTime = subscriptionCompletedDateTime;
  }
  
  public void receiveTermination(LocalDateTime terminationReceivedDateTime) {
    if (!isSubscriptionCompleted()) {
      throw new IllegalStateException();
    }
    this.terminationReceivedDateTime = terminationReceivedDateTime;
    this.cancelTerminationReceiptDateTime = null;
  }
  
  public void completeTermination(LocalDateTime terminationCompletedDateTime) {
    if (!isSubscriptionCompleted()) {
      throw new IllegalStateException();
    }
    this.terminationCompletedDateTime = terminationCompletedDateTime;
  }
  
  public void cancelTerminationReceipt(LocalDateTime cancelTerminationReceiptDateTime) {
    if (!isTerminationReceived()) {
      throw new IllegalStateException();
    }
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
