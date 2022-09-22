package team.caltech.olmago.contract.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(indexes = @Index(name = "message_envelope_n1", columnList = "publihsed, id"))
public class MessageEnvelope {
  private final static ObjectMapper objectMapper = new ObjectMapper();
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String uuid;
  
  @Column(nullable = false)
  private String aggregateType;
  
  @Column(nullable = false)
  private String aggregateId;
  
  @Column(nullable = false)
  private String bindingName;
  
  @Column(nullable = false)
  private LocalDateTime createdAt;
  
  @Column(nullable = true)
  private LocalDateTime publishedAt;
  
  @Column(nullable = false)
  private boolean published;
  
  @Column(nullable = false)
  private String eventType;
  
  @Column(nullable = false)
  private String payload;
  
  @Builder
  public static MessageEnvelope wrap(String aggregateType,
                                     String aggregateId,
                                     String bindingName,
                                     String eventType,
                                     Object payload) throws JsonProcessingException {
    MessageEnvelope dee = new MessageEnvelope();
    dee.uuid = UUID.randomUUID().toString();
    dee.aggregateType = aggregateType;
    dee.aggregateId = aggregateId;
    dee.bindingName = bindingName;
    dee.createdAt = LocalDateTime.now();
    // ex: ContractSubscriptionCompleted -> contractSubscriptionCompleted
    dee.eventType = firstLetterToLowerCase(eventType);
    dee.payload = objectMapper.writeValueAsString(payload);
    dee.published = false;
    return dee;
  }
  
  private static String firstLetterToLowerCase(String eventType) {
    return eventType.substring(0,1).toLowerCase() + eventType.substring(1);
  }
  
  public void publish(LocalDateTime dtm) {
    publishedAt = dtm;
    published = true;
  }
}
