package team.caltech.olmago.contract.messagebus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.common.message.MessageEnvelope;
import team.caltech.olmago.contract.common.message.MessageEnvelopeRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageBus {
  public static final String HEADER_MESSAGE_NAME = "type";
  public static final String HEADER_UUID = "uuid";
  
  private final MessageEnvelopeRepository messageEnvelopeRepository;
  private final StreamBridge streamBridge;
  
  @Scheduled(fixedDelay = 2000)
  public void sendMessages() {
    messageEnvelopeRepository.findByPublished(false).forEach(this::sendMessage);
  }
  
  @Transactional
  private void sendMessage(MessageEnvelope dee) {
    Message<String> message = MessageBuilder.withPayload(dee.getPayload())
        .setHeader(HEADER_MESSAGE_NAME, dee.getMessageName())
        .setHeader(HEADER_UUID, dee.getUuid())
        .build();
    
    if (streamBridge.send(dee.getBindingName(), message)) {
      dee.publish(LocalDateTime.now());
    }
    else {
      throw new RuntimeException();
    }
  }
}
