package team.caltech.olmago.contract.domain.contract.uzoopackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UzooPackageRepository extends JpaRepository<UzooPackage, Long>, UzooPackageRepositoryCustom {
  Optional<UzooPackage> findByLastOrderId(long lastOrderId);
}
