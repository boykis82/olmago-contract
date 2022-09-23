package team.caltech.olmago.contract.domain.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.domain.contract.Contract;
import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.discount.condition.DiscountCondition;
import team.caltech.olmago.contract.domain.exception.InvalidArgumentException;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RelatedOptionProductDcCond extends DiscountCondition {
  private final ContractRepository contractRepository;
  private List<String> optionProductCodes;

  public static RelatedOptionProductDcCond with(ContractRepository contractRepository) {
    return new RelatedOptionProductDcCond(contractRepository);
  }
  
  public RelatedOptionProductDcCond in(List<String> optionProductCodes) {
    this.optionProductCodes = optionProductCodes;
    return this;
  }
  
  @Override
  public boolean satisfied(Contract contract) {
    return optionProductCodes.stream()
        .anyMatch(pc -> contractRepository.findOptionContractByPackageContract(contract)
            .orElseThrow(InvalidArgumentException::new)
            .getFeeProductCode().equals(pc)
        );
  }
}
