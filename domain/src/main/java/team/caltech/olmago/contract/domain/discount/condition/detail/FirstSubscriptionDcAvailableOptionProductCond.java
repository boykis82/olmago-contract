package team.caltech.olmago.contract.domain.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.discount.condition.DiscountCondition;
import team.caltech.olmago.contract.domain.exception.InvalidArgumentException;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FirstSubscriptionDcAvailableOptionProductCond extends DiscountCondition {
  private final ContractRepository contractRepository;
  private final ProductRepository productRepository;

  public static FirstSubscriptionDcAvailableOptionProductCond with(ContractRepository contractRepository, ProductRepository productRepository) {
    return new FirstSubscriptionDcAvailableOptionProductCond(contractRepository, productRepository);
  }
  
  @Override
  public boolean satisfied(Contract contract) {
    String optionProductCode = contractRepository.findOptionContractByPackageContract(contract)
        .orElseThrow(InvalidArgumentException::new)
        .getFeeProductCode();
    
    return productRepository.findById(optionProductCode)
        .orElseThrow(InvalidArgumentException::new)
        .isTheFirstSubscriptionDcTarget();
  }
}
