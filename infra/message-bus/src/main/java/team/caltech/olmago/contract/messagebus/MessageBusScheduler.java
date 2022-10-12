package team.caltech.olmago.contract.messagebus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.caltech.olmago.contract.common.message.MessageEnvelope;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class MessageBusScheduler {
  private final MessageBus messageBus;
  
  @Scheduled(fixedDelay = 5000)
  public void fetchMessagesAndSend() {
    List<MessageEnvelope> msgs = messageBus.findNotSentMessageEnvelopes();
    msgs.forEach(messageBus::sendMessage);
    log.info(msgs.size() + " processed!");
  }
  
}
