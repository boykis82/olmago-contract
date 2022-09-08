package team.caltech.olmago.contract.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UzooPackageRepository extends JpaRepository<UzooPackage, Long> {
  @Query(
      "SELECT p " +
      "FROM   UzooPackage p " +
      "WHERE  p.packageContract = :pkgContract " +
      "AND    p.optionContract = :optContract " +
      "AND    p.lifeCycle.terminationCompletedDateTime IS NULL" +
      "AND    p.lifeCycle.terminationReceivedDateTime IS NULL" +
      "AND    p.lifeCycle.subscriptionReceivedDateTime IS NOT NULL" +
      "AND    p.lifeCycle.subscriptionCompletedDateTime IS NOT NULL"
  )
  Optional<UzooPackage> findActivePackage(@Param("pkgContract") Contract pkgContract, @Param("optContract") Contract optContract);

  @Query(
      "SELECT p " +
      "FROM   UzooPackage p " +
      "WHERE  (p.packageContract = :contract OR p.optionContract = :contract)" +
      "AND    p.lifeCycle.terminationCompletedDateTime IS NULL" +
      "AND    p.lifeCycle.terminationReceivedDateTime IS NULL" +
      "AND    p.lifeCycle.subscriptionReceivedDateTime IS NOT NULL" +
      "AND    p.lifeCycle.subscriptionCompletedDateTime IS NULL"
  )
  Optional<UzooPackage> findSubscriptionReceivedPackage(@Param("contract") Contract contract);

  @Query(
      "SELECT p " +
      "FROM   UzooPackage p " +
      "WHERE  p.packageContract = :pkgContract " +
      "AND    p.optionContract = :optContract " +
      "AND    p.lifeCycle.terminationCompletedDateTime IS NULL" +
      "AND    p.lifeCycle.terminationReceivedDateTime IS NOT NULL" +
      "AND    p.lifeCycle.subscriptionReceivedDateTime IS NOT NULL" +
      "AND    p.lifeCycle.subscriptionCompletedDateTime IS NOT NULL"
  )
  Optional<UzooPackage> findTerminationReceivedPackage(@Param("pkgContract") Contract pkgContract, @Param("optContract") Contract optContract);

  @Query(
      "SELECT p " +
      "FROM   UzooPackage p " +
      "WHERE  (p.packageContract = :contract OR p.optionContract = :contract)" +
      "AND    p.lifeCycle.terminationCompletedDateTime IS NULL" +
      "AND    p.lifeCycle.terminationReceivedDateTime IS NOT NULL" +
      "AND    p.lifeCycle.subscriptionReceivedDateTime IS NOT NULL" +
      "AND    p.lifeCycle.subscriptionCompletedDateTime IS NOT NULL"
  )
  Optional<UzooPackage> findTerminationReceivedPackage(@Param("contract") Contract contract);
}
