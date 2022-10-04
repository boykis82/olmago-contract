package team.caltech.olmago.contract.productauth.message.in.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MessageInBox {
  @Id
  @Column(name = "key")
  private String key;
  
  @Column(name = "rcv_dtm", nullable = false)
  private LocalDateTime receivedDateTime;
  
  @Column(name = "event_type", nullable = false)
  private String eventType;
  
  @Column(name = "payload", nullable = false, length = 2048)
  private String payload;
}
