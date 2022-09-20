package team.caltech.olmago.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class DomainEventEnvelope {
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
  private String channelName;
  
  @Column(nullable = false)
  private LocalDateTime createdAt;
  
  @Column(nullable = true)
  private LocalDateTime publishedAt;
  
  @Column(nullable = false)
  private String eventType;
  
  @Column(nullable = false)
  private String payload;
  
  @Builder
  public static DomainEventEnvelope wrap(String aggregateType,
                                         String aggregateId,
                                         String channelName,
                                         String eventType,
                                         Object payload) throws JsonProcessingException {
    DomainEventEnvelope dee = new DomainEventEnvelope();
    dee.uuid = UUID.randomUUID().toString();
    dee.aggregateType = aggregateType;
    dee.aggregateId = aggregateId;
    dee.channelName = channelName;
    dee.createdAt = LocalDateTime.now();
    dee.eventType = eventType;
    dee.payload = objectMapper.writeValueAsString(payload);
    return dee;
  }
}
