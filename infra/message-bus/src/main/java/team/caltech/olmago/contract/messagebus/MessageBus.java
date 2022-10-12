package team.caltech.olmago.contract.messagebus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.common.message.MessageEnvelope;
import team.caltech.olmago.contract.common.message.MessageEnvelopeRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageBus {
  public static final String HEADER_MESSAGE_NAME = "message-name";
  public static final String HEADER_UUID = "uuid";
  
  private final StreamBridge streamBridge;
  private final MessageEnvelopeRepository messageEnvelopeRepository;
  
  public List<MessageEnvelope> findNotSentMessageEnvelopes() {
    return messageEnvelopeRepository.findByPublished(false);
  }
  
  public void sendMessage(MessageEnvelope dee) {
    Message<String> message = MessageBuilder.withPayload(dee.getPayload())
        .setHeader(HEADER_MESSAGE_NAME, dee.getMessageName())
        .setHeader(HEADER_UUID, dee.getUuid())
        .build();
    
    if (streamBridge.send(dee.getBindingName(), message)) {
      log.info(dee.getId() + " - " + dee.getUuid() + " processed!");
      dee.publish(LocalDateTime.now());
      messageEnvelopeRepository.save(dee);
    }
    else {
      log.info("streamBridge send error!");
      throw new RuntimeException();
    }
  }
}
