package team.caltech.olmago.contract.event.in.common;

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
public class EventInBox {
  @Id
  private String key;
  
  @Column(nullable = false)
  private LocalDateTime receivedDateTime;
  
  @Column(nullable = false)
  private String eventType;
  
  @Column(nullable = false)
  private String payload;
}
