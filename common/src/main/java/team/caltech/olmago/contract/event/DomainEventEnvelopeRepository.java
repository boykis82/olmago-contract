package team.caltech.olmago.contract.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DomainEventEnvelopeRepository extends JpaRepository<DomainEventEnvelope, Long> {
  @Query(
      "SELECT dee FROM DomainEventEnvelope WHERE published = :published ORDER BY id"
  )
  List<DomainEventEnvelope> findByPublished(@Param("published") boolean published);
}
