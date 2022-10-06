package team.caltech.olmago.contract.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.contract.uzoopackage.UzooPackage;
import team.caltech.olmago.contract.domain.contract.uzoopackage.UzooPackageRepository;
import team.caltech.olmago.contract.domain.exception.InvalidArgumentException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PackageServiceImpl implements PackageService {
  private final UzooPackageRepository uzooPackageRepository;
  
  @Override
  public void createPackage(Contract pkgContract, Contract optContract, LocalDateTime pkgStartDateTime, long orderId) {
    UzooPackage pkg = uzooPackageRepository.save(
        UzooPackage.builder()
            .packageContract(pkgContract)
            .optionContract(optContract)
            .subscriptionReceivedDateTime(pkgStartDateTime)
            .lastOrderId(orderId)
            .build()
    );
  }
  
  @Override
  public void completePackageSubscription(Contract contract, LocalDateTime subscriptionCompletedDateTime) {
    uzooPackageRepository.findSubscriptionReceivedPackage(contract)
        .ifPresent(p -> p.completeSubscription(subscriptionCompletedDateTime));
  }
  
  @Override
  public void receiveTermination(Contract pkgContract, Contract optContract, LocalDateTime terminationReceivedDateTime, long orderId) {
    uzooPackageRepository.findActivePackage(pkgContract, optContract)
        .orElseThrow(InvalidArgumentException::new)
        .receiveTermination(terminationReceivedDateTime, orderId);
  }
  
  @Override
  public void cancelTerminationReceipt(LocalDateTime cancelTerminationReceipt, long orderId) {
    uzooPackageRepository.findByLastOrderId(orderId)
        .ifPresentOrElse(
            p -> p.cancelTerminationReceipt(cancelTerminationReceipt),
            () -> {}
        );
  }
  
  @Override
  public void completePackageTermination(Contract contract, LocalDateTime terminationCompletedDateTime) {
    uzooPackageRepository.findTerminationReceivedPackage(contract)
        .ifPresent(p -> p.completeSubscription(terminationCompletedDateTime));
  }
  
  @Override
  public void changePackageComposition(Contract pkgContract, Contract bfOptContract, Contract afOptContract, LocalDateTime changeDateTime, long orderId) {
    receiveTermination(pkgContract, bfOptContract, changeDateTime, orderId);
    createPackage(pkgContract, afOptContract, changeDateTime, orderId);
  }
  
  @Override
  public void cancelPackageSubscriptionReceipt(Contract contract, LocalDateTime subscriptionCanceledDateTime) {
    uzooPackageRepository.findSubscriptionReceivedPackage(contract)
        .ifPresent(p -> p.cancelSubscriptionReceipt(subscriptionCanceledDateTime));
  }
}
