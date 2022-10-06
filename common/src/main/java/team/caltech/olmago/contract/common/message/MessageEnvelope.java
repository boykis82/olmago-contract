package team.caltech.olmago.contract.common.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "msg_envelope", indexes = @Index(name = "message_envelope_n1", columnList = "published,id"))
public class MessageEnvelope {
  private final static ObjectMapper objectMapper;
  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }
  
  enum MessageType {
    EVENT,
    COMMAND
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "uuid", nullable = false)
  private String uuid;
  
  @Column(name = "msg_typ", nullable = false)
  @Enumerated(EnumType.STRING)
  private MessageType messageType;
  
  /* 여기서부터는 event에서만 유효 */
  @Column(name = "agg_typ")
  private String aggregateType;
  
  @Column(name = "agg_id")
  private String aggregateId;
  
  @Column(name = "bind_nm", nullable = false)
  private String bindingName;
  
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
  
  @Column(name = "published_at")
  private LocalDateTime publishedAt;
  
  @Column(name = "published", nullable = false)
  private boolean published;
  
  @Column(name = "msg_name", nullable = false)
  private String messageName;
  
  @Column(name = "payload", length = 2048, nullable = false)
  private String payload;
  
  @Builder
  public static MessageEnvelope wrapEvent(String aggregateType,
                                          String aggregateId,
                                          String bindingName,
                                          String eventName,
                                          Object payload) throws JsonProcessingException {
    MessageEnvelope dee = new MessageEnvelope();
    dee.uuid = UUID.randomUUID().toString();
    dee.messageType = MessageType.EVENT;
    dee.aggregateType = aggregateType;
    dee.aggregateId = aggregateId;
    dee.bindingName = bindingName;
    dee.createdAt = LocalDateTime.now();
    // ex: ContractSubscriptionCompleted -> contractSubscriptionCompleted
    dee.messageName = firstLetterToLowerCase(eventName);
    dee.payload = objectMapper.writeValueAsString(payload);
    dee.published = false;
    return dee;
  }
  
  @Builder
  public static MessageEnvelope wrapCommand(String bindingName,
                                            String eventType,
                                            Object payload) throws JsonProcessingException {
    MessageEnvelope dee = new MessageEnvelope();
    dee.uuid = UUID.randomUUID().toString();
    dee.messageType = MessageType.COMMAND;
    dee.bindingName = bindingName;
    dee.createdAt = LocalDateTime.now();
    // ex: ContractSubscriptionCompleted -> contractSubscriptionCompleted
    dee.messageName = firstLetterToLowerCase(eventType);
    dee.payload = objectMapper.writeValueAsString(payload);
    dee.published = false;
    return dee;
  }
  
  private static String firstLetterToLowerCase(String msgName) {
    return msgName.substring(0,1).toLowerCase() + msgName.substring(1);
  }
  
  public void publish(LocalDateTime dtm) {
    publishedAt = dtm;
    published = true;
  }
}