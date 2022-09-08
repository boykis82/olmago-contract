package team.caltech.olmago.contract.discount.condition.detail;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.contract.Contract;
import team.caltech.olmago.contract.contract.ContractType;
import team.caltech.olmago.contract.discount.condition.DiscountCondition;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractTypeDcCond extends DiscountCondition {
  private final ContractType contractType;
  
  public static ContractTypeDcCond asPackage() {
    return new ContractTypeDcCond(ContractType.PACKAGE);
  }
  
  public static ContractTypeDcCond asOption() {
    return new ContractTypeDcCond(ContractType.OPTION);
  }
  
  public static ContractTypeDcCond asUnit() {
    return new ContractTypeDcCond(ContractType.UNIT);
  }
  
  @Override
  public boolean satisfied(Contract contract) {
    return contractType == contract.getContractType();
  }
}

