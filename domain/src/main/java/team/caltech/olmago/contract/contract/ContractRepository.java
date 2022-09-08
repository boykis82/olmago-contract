package team.caltech.olmago.contract.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.caltech.olmago.contract.plm.DiscountType;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
  @Query(
      "SELECT COUNT(*) " +
      "FROM   Contract c " +
      "       INNER JOIN " +
      "       c.productSubscriptions ps " +
      "       INNER JOIN " +
      "       ps.discountSubscriptions ds " +
      "       INNER JOIN " +
      "       ds.discountPolicy dp " +
      "WHERE  c.customerId = :customerId " +
      "AND    c.lifeCycle.subscriptionCompletedDateTime IS NOT NULL" +
      "AND    dp.dcType = :dcType"
  )
  int countAppliedDcType(@Param("customerId") long customerId, @Param("dcType") DiscountType dcType);

  @Query(
      "SELECT COUNT(*) " +
      "FROM   Contract c " +
      "WHERE  c.customerId = :customerId " +
      "AND    c.feeProductCode = :feeProductCodes " +
      "AND    c.lifeCycle.terminationCompletedDateTime IS NOT NULL"
  )
  int countActiveContractByCustomerIdAndFeeProductCode(
      @Param("customerId") long customerId,
      @Param("feeProductCode") String feeProductCode
  );
  
  @Query(
      "SELECT p.optContract " +
      "FROM   UzooPackage p " +
      "WHERE  p.pkgContract = :pkgContract " +
      "AND    c.lifeCycle.terminationCompletedDateTime IS NULL" +
      "AND    c.lifeCycle.terminationReceivedDateTime IS NULL"
  )
  Optional<Contract> findOptionContractByPackageContract(
      @Param("pkgContract") Contract pkgContract
  );
}
