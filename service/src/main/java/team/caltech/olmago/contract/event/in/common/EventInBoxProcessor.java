package team.caltech.olmago.contract.event.in.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class EventInBoxProcessor {
  private final static String HEADER_EVENT_TYPE = "type";
  private final static String HEADER_UUID = "uuid";
  
  private final EventInBoxRepository eventInBoxRepository;
  private final ObjectMapper objectMapper;
  
  public boolean notExistedEvent(Message<?> message) {
    return eventInBoxRepository.findById(getUUID(message)).isPresent();
  }
  
  public void saveInBoxEvent(Message<?> message) {
    EventInBox eventInBox;
    try {
      eventInBox = new EventInBox(
          getUUID(message),
          LocalDateTime.now(),
          getEventType(message),
          objectMapper.writeValueAsString(message.getPayload())
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    eventInBoxRepository.save(eventInBox);
  }
  
  private String getUUID(Message<?> message) {
    return (String)message.getHeaders().get(HEADER_UUID);
  }
  
  private String getEventType(Message<?> message) {
    return (String)message.getHeaders().get(HEADER_EVENT_TYPE);
  }
  
}
