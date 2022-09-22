package team.caltech.olmago.contract.event.in.common;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventInBoxRepository extends JpaRepository<EventInBox, String> {
}
