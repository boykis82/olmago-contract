package team.caltech.olmago.contract.contract.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Event {
  protected final UUID uuid;
  protected final LocalDateTime createdAt;
  
  protected Event() {
    uuid = UUID.randomUUID();
    createdAt = LocalDateTime.now();
  }
}
