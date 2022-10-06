package team.caltech.olmago.contract.service.service;

import team.caltech.olmago.contract.domain.contract.Contract;

import java.time.LocalDateTime;

public interface PackageService {
  void createPackage(Contract pkgContract, Contract optContract, LocalDateTime pkgStartDateTime, long orderId);
  void completePackageSubscription(Contract contract, LocalDateTime subscriptionCompletedDateTime);
  void receiveTermination(Contract pkgContract, Contract optContract, LocalDateTime terminationReceivedDateTime, long orderId);
  void cancelTerminationReceipt(LocalDateTime cancelTerminationReceipt, long orderId);
  void completePackageTermination(Contract contract, LocalDateTime terminationCompletedDateTime);
  void changePackageComposition(Contract pkgContract, Contract bfOptContract, Contract afOptContract, LocalDateTime changeDateTime, long orderId);
  void cancelPackageSubscriptionReceipt(Contract contract, LocalDateTime subscriptionCanceledDateTime);
}
