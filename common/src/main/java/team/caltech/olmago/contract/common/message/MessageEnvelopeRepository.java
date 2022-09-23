package team.caltech.olmago.contract.common.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageEnvelopeRepository extends JpaRepository<MessageEnvelope, Long> {
  @Query(
      "SELECT dee FROM MessageEnvelope dee WHERE published = :published ORDER BY id"
  )
  List<MessageEnvelope> findByPublished(@Param("published") boolean published);
}
