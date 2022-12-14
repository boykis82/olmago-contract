package team.caltech.olmago.contract.domain.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.discount.condition.DiscountCondition;
import team.caltech.olmago.contract.domain.plm.discount.DiscountType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
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
