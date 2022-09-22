package team.caltech.olmago.contract.message.in.common;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageInBoxRepository extends JpaRepository<MessageInBox, String> {
}
