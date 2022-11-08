package team.caltech.olmago.contract.productauth.message.out;

import team.caltech.olmago.common.message.MessageEnvelope;

public interface MessagePublisher {
  void sendMessage(MessageEnvelope msg);
}
