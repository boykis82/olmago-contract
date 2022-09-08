package team.caltech.olmago.contract.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.uzoopackage.UzooPackage;
import team.caltech.olmago.contract.contract.uzoopackage.UzooPackageRepository;
import team.caltech.olmago.contract.exception.InvalidArgumentException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PackageServiceImpl implements PackageService {
  private final UzooPackageRepository uzooPackageRepository;
  
  @Override
  public void createPackage(Contract pkgContract, Contract optContract, LocalDateTime pkgStartDateTime) {
    UzooPackage pkg = uzooPackageRepository.save(
        UzooPackage.builder()
            .packageContract(pkgContract)
            .optionContract(optContract)
            .subscriptionReceivedDateTime(pkgStartDateTime)
            .build()
    );
    pkgContract.setPackageId(pkg.getId());
    optContract.setPackageId(pkg.getId());
  }
  
  @Override
  public void completePackageSubscription(Contract contract, LocalDateTime subscriptionCompletedDateTime) {
    uzooPackageRepository.findSubscriptionReceivedPackage(contract)
        .ifPresent(p -> p.completeSubscription(subscriptionCompletedDateTime));
  }
  
  @Override
  public void receiveTermination(Contract pkgContract, Contract optContract, LocalDateTime terminationReceivedDateTime) {
    uzooPackageRepository.findActivePackage(pkgContract, optContract)
        .orElseThrow(InvalidArgumentException::new)
        .receiveTermination(terminationReceivedDateTime);
  }
  
  @Override
  public void cancelTerminationReceipt(Contract pkgContract, Contract optContract, LocalDateTime cancelTerminationReceipt) {
    uzooPackageRepository.findTerminationReceivedPackage(pkgContract, optContract)
        .orElseThrow(InvalidArgumentException::new)
        .cancelTerminationReceipt(cancelTerminationReceipt);
  }
  
  @Override
  public void completePackageTermination(Contract contract, LocalDateTime terminationCompletedDateTime) {
    uzooPackageRepository.findTerminationReceivedPackage(contract)
        .ifPresent(p -> p.completeSubscription(terminationCompletedDateTime));
  }
  
  @Override
  public void changePackageComposition(Contract pkgContract, Contract bfOptContract, Contract afOptContract, LocalDateTime changeDateTime) {
    receiveTermination(pkgContract, bfOptContract, changeDateTime);
    createPackage(pkgContract, afOptContract, changeDateTime);
  }
}
