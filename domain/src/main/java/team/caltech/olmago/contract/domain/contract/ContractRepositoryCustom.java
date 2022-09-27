package team.caltech.olmago.contract.domain.contract;

import team.caltech.olmago.contract.domain.plm.discount.DiscountType;

import java.util.List;
import java.util.Optional;

public interface ContractRepositoryCustom {
  Long countAppliedDcTypeByCustomer(long customerId, DiscountType dcType);
  Long countActiveContractByCustomerAndFeeProductCode(long customerId, String feeProductCode);
  Optional<Contract> findOptionContractByPackageContract(Contract pkgContract);
  List<Contract> findByCustomerAndOrderId(long customerId, long orderId);
  List<Contract> findByCustomerId(long customerId, boolean includeTerminatedContract);
  List<Contract> findByContractId(long contractId, boolean withPackageOrOption, boolean includeProductSubscription, boolean includeDiscountSubscription);
  Optional<Contract> findWithProductsAndDiscountsById(long contractId);
}
