package team.caltech.olmago.contract;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainEventEnvelopeRepository extends JpaRepository<DomainEventEnvelope, Long> {
}
