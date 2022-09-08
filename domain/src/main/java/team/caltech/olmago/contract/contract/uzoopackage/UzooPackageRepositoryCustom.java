package team.caltech.olmago.contract.contract.uzoopackage;

import team.caltech.olmago.contract.contract.Contract;

import java.util.Optional;

public interface UzooPackageRepositoryCustom {
  Optional<UzooPackage> findActivePackage(Contract pkgContract, Contract optContract);
  Optional<UzooPackage> findSubscriptionReceivedPackage(Contract contract);
  Optional<UzooPackage> findTerminationReceivedPackage(Contract pkgContract, Contract optContract);
  Optional<UzooPackage> findTerminationReceivedPackage(Contract contract);
}
