package team.caltech.olmago.contract.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.ContractRepository;
import team.caltech.olmago.contract.discount.condition.DiscountCondition;
import team.caltech.olmago.contract.plm.DiscountType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FirstSubscriptionDcCond extends DiscountCondition {
  private final ContractRepository contractRepository;
  
  public static FirstSubscriptionDcCond with(ContractRepository contractRepository) {
    return new FirstSubscriptionDcCond(contractRepository);
  }
  
  @Override
  public boolean satisfied(Contract contract) {
    return contractRepository.countAppliedDcTypeByCustomer(contract.getCustomerId(), DiscountType.THE_FIRST_SUBSCRIPTION) == 0;
  }
}
