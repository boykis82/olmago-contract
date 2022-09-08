package team.caltech.olmago.contract.product.factory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.caltech.olmago.contract.product.factory.ProductRelation.ProductRelationType;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRelationRepository extends JpaRepository<ProductRelation, Long> {
  @Query(
      "SELECT pr.subProductId " +
      "FROM   ProductRelation pr " +
      "WHERE  pr.mainProductId = :mainProductId " +
      "AND    pr.productRelationType = :productRelationType " +
      "AND    :strdDate BETWEEN pr.startDt AND pr.endDt "
  )
  List<String> findByMainProductAndProductRelationType(
      @Param("mainProductId") String mainProductId,
      @Param("productRelationType") ProductRelationType productRelationType,
      @Param("strdDate") LocalDate strdDate
  );
}
