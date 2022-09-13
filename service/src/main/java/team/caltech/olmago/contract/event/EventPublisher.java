package team.caltech.olmago.contract.event;

import team.caltech.olmago.contract.contract.event.Event;

import java.util.List;

public interface EventPublisher {
  void fire(String channel, Event e);
  void fire(String channel, List<? extends Event> e);
}
