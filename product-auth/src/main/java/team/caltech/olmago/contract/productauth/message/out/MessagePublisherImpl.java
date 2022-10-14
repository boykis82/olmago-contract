package team.caltech.olmago.contract.productauth.message.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.common.message.MessageEnvelope;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessagePublisherImpl implements MessagePublisher {
  public static final String HEADER_MESSAGE_NAME = "message-name";
  public static final String HEADER_UUID = "uuid";
  
  private final StreamBridge streamBridge;
  
  @Transactional
  @Override
  public void sendMessage(MessageEnvelope msg) {
    Message<String> message = MessageBuilder.withPayload(msg.getPayload())
        .setHeader(HEADER_MESSAGE_NAME, msg.getMessageName())
        .setHeader(HEADER_UUID, msg.getUuid())
        .build();
    
    if (streamBridge.send(msg.getBindingName(), message)) {
      log.info(msg.getId() + " - " + msg.getUuid() + " processed!");
    }
    else {
      log.info("streamBridge send error!");
      throw new RuntimeException();
    }
  }
}
