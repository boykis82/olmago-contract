package team.caltech.olmago.contract.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomainEventEnvelopeRepository extends JpaRepository<DomainEventEnvelope, Long> {
  @Query(
      "SELECT dee FROM DomainEventEnvelope dee WHERE published = :published ORDER BY id"
  )
  List<DomainEventEnvelope> findByPublished(@Param("published") boolean published);
}
