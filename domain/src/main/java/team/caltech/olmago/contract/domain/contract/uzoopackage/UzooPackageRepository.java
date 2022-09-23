package team.caltech.olmago.contract.domain.contract.uzoopackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UzooPackageRepository extends JpaRepository<UzooPackage, Long>, UzooPackageRepositoryCustom {

}
