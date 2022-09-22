package team.caltech.olmago.contract.service;

import team.caltech.olmago.contract.contract.Contract;

import java.time.LocalDateTime;

public interface PackageService {
  void createPackage(Contract pkgContract, Contract optContract, LocalDateTime pkgStartDateTime);
  void completePackageSubscription(Contract contract, LocalDateTime subscriptionCompletedDateTime);
  void receiveTermination(Contract pkgContract, Contract optContract, LocalDateTime terminationReceivedDateTime);
  void cancelTerminationReceipt(Contract pkgContract, Contract optContract, LocalDateTime cancelTerminationReceipt);
  void completePackageTermination(Contract contract, LocalDateTime terminationCompletedDateTime);
  void changePackageComposition(Contract pkgContract, Contract bfOptContract, Contract afOptContract, LocalDateTime changeDateTime);
  void cancelPackageSubscriptionReceipt(Contract contract, LocalDateTime subscriptionCanceledDateTime);
}
