package team.caltech.olmago.contract.productauth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductAuthRepository extends JpaRepository<ProductAuth, ProductAuthId> {
  @Query("SELECT pa FROM ProductAuth pa WHERE pa.id.contractId = :contractId")
  List<ProductAuth> findByContractId(@Param("contractId") long contractId);
}
