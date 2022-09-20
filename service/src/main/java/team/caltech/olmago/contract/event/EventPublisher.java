package team.caltech.olmago.contract.event;

import team.caltech.olmago.contract.contract.event.ContractEventBase;

import java.util.List;

public interface EventPublisher {
  void fire(String channel, ContractEventBase e);
  void fire(String channel, List<? extends ContractEventBase> e);
}
