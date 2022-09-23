package team.caltech.olmago.contract.service.message.out;

import team.caltech.olmago.contract.common.message.MessageEnvelope;

import java.util.List;

public interface MessageStore {
  void saveMessage(MessageEnvelope msg);
  void saveMessage(List<MessageEnvelope> msgs);
}
