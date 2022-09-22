package team.caltech.olmago.contract.messagebus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.event.DomainEventEnvelope;
import team.caltech.olmago.contract.event.DomainEventEnvelopeRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageBus {
  public static final String HEADER_EVENT_TYPE = "type";
  public static final String HEADER_UUID = "uuid";
  
  private final DomainEventEnvelopeRepository domainEventEnvelopeRepository;
  private final StreamBridge streamBridge;
  
  @Scheduled(fixedDelay = 2000)
  public void sendMessages() {
    List<DomainEventEnvelope> domainEventEnvelopes = domainEventEnvelopeRepository.findByPublished(false);
    domainEventEnvelopes.forEach(this::sendMessage);
  }
  
  @Transactional
  private void sendMessage(DomainEventEnvelope dee) {
    Message<String> message = MessageBuilder.withPayload(dee.getPayload())
        .setHeader(HEADER_EVENT_TYPE, dee.getEventType())
        .setHeader(HEADER_UUID, dee.getUuid())
        .build();
    
    streamBridge.send(dee.getBindingName(), message);
  
    dee.publish(LocalDateTime.now());
  }
}
