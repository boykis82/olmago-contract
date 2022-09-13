package team.caltech.olmago.contract.contract;

import team.caltech.olmago.contract.plm.DiscountType;

import java.util.List;
import java.util.Optional;

public interface ContractRepositoryCustom {
  Long countAppliedDcTypeByCustomer(long customerId, DiscountType dcType);
  Long countActiveContractByCustomerAndFeeProductCode(long customerId, String feeProductCode);
  Optional<Contract> findOptionContractByPackageContract(Contract pkgContract);
  List<Contract> findByCustomerAndOrderId(long customerId, long orderId);
}
