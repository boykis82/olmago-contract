package team.caltech.olmago.contract.domain.contract;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.domain.plm.discount.DiscountType;

import java.util.List;
import java.util.Optional;

import static team.caltech.olmago.contract.domain.contract.QContract.contract;
import static team.caltech.olmago.contract.domain.contract.uzoopackage.QUzooPackage.uzooPackage;
import static team.caltech.olmago.contract.domain.discount.QDiscountSubscription.discountSubscription;
import static team.caltech.olmago.contract.domain.product.QProductSubscription.*;
import static team.caltech.olmago.contract.domain.plm.discount.QDiscountPolicy.discountPolicy;
import static team.caltech.olmago.contract.domain.plm.product.QProduct.product;

@RequiredArgsConstructor
public class ContractRepositoryImpl implements ContractRepositoryCustom {
  private final JPAQueryFactory jpaQueryFactory;
  
  @Override
  public Long countAppliedDcTypeByCustomer(long customerId, DiscountType dcType) {
    return jpaQueryFactory
        .select(contract.count())
        .from(contract)
        .join(contract.productSubscriptions, productSubscription).fetchJoin()
        .join(productSubscription.discountSubscriptions, discountSubscription).fetchJoin()
        .join(discountSubscription.discountPolicy, discountPolicy).fetchJoin()
        .where(
            contract.customerId.eq(customerId)
                .and(contract.lifeCycle.subscriptionCompletedDateTime.isNotNull())
                .and(discountPolicy.dcType.eq(dcType))
        )
        .distinct()
        .fetchOne();
  }
  
  @Override
  public Optional<Contract> findWithProductsAndDiscountsById(long contractId) {
    return Optional.ofNullable(
        jpaQueryFactory
            .select(contract)
            .from(contract)
            .join(contract.productSubscriptions, productSubscription).fetchJoin()
            .join(productSubscription.product, product).fetchJoin()
            .join(productSubscription.discountSubscriptions, discountSubscription).fetchJoin()
            .join(discountSubscription.discountPolicy, discountPolicy).fetchJoin()
            .where(
                contract.id.eq(contractId)
            )
            .distinct()
            .fetchOne()
    );
  }
  
  @Override
  public Long countActiveContractByCustomerAndFeeProductCode(long customerId, String feeProductCode) {
    return jpaQueryFactory
        .select(contract.count())
        .from(contract)
        .where(
            contract.customerId.eq(customerId)
                .and(contract.feeProductCode.eq(feeProductCode))
                .and(contract.lifeCycle.terminationCompletedDateTime.isNull())
        )
        .fetchOne();
  }
  
  @Override
  public Optional<Contract> findOptionContractByPackageContract(Contract pkgContract) {
    return Optional.ofNullable(
        jpaQueryFactory
            .select(uzooPackage.optionContract)
            .from(uzooPackage)
            .where(
                uzooPackage.packageContract.eq(pkgContract)
                    .and(uzooPackage.lifeCycle.terminationCompletedDateTime.isNull())
                    .and(uzooPackage.lifeCycle.terminationReceivedDateTime.isNull())
            )
            .fetchOne()
    );
  }
  
  @Override
  public List<Contract> findByCustomerAndOrderId(long customerId, long orderId) {
    return jpaQueryFactory
        .select(contract)
        .where(
          contract.customerId.eq(customerId)
              .and(contract.lastOrderId.eq(orderId))
        )
        .fetch();
  }

  @Override
  public List<Contract> findByCustomerId(long customerId, boolean includeTerminatedContract) {
    return jpaQueryFactory
        .select(contract)
        .where(
            contract.customerId.eq(customerId)
                .and(includeTerminatedContractExpr(includeTerminatedContract))
        )
        .fetch();
  }
  
  private BooleanExpression includeTerminatedContractExpr(boolean includeTerminatedContract) {
    return includeTerminatedContract ? null : contract.lifeCycle.terminationCompleted.isNull();
  }
  
  @Override
  public List<Contract> findByContractId(long contractId, boolean withPackageOrOption) {
    if (withPackageOrOption) {
      return jpaQueryFactory
          .select(contract)
          .where(contract.id.eq(contractId))
          .fetch();
    }
    else {
      return jpaQueryFactory.select(contract)
          .from(uzooPackage)
          .join(uzooPackage.packageContract, contract)
          .join(uzooPackage.optionContract, contract)
          .where(packageOrOptionContract(contractId))
          .distinct()
          .fetch();
    }
  }
  
  private BooleanExpression packageOrOptionContract(long contractId) {
    return uzooPackage.packageContract.id.eq(contractId).or(uzooPackage.optionContract.id.eq(contractId));
  }
  
}
