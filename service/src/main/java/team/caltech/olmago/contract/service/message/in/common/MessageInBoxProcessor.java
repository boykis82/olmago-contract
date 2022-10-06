package team.caltech.olmago.contract.service.message.in.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class MessageInBoxProcessor {
  private final static String HEADER_MESSAGE_NAME = "message-name";
  private final static String HEADER_UUID = "uuid";
  
  private final MessageInBoxRepository messageInBoxRepository;
  private final ObjectMapper objectMapper;
  
  public boolean notExistedMessage(Message<?> message) {
    return messageInBoxRepository.findById(getUUID(message)).isPresent();
  }
  
  public void saveInBoxMessage(Message<?> message) {
    MessageInBox messageInBox;
    try {
      messageInBox = new MessageInBox(
          getUUID(message),
          LocalDateTime.now(),
          getMessageName(message),
          objectMapper.writeValueAsString(message.getPayload())
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    messageInBoxRepository.save(messageInBox);
  }
  
  private String getUUID(Message<?> message) {
    return (String)message.getHeaders().get(HEADER_UUID);
  }
  
  private String getMessageName(Message<?> message) {
    return (String)message.getHeaders().get(HEADER_MESSAGE_NAME);
  }
  
}
