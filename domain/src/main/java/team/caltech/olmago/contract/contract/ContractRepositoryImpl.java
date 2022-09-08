package team.caltech.olmago.contract.contract;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.plm.DiscountType;

import java.util.Optional;

import static team.caltech.olmago.contract.contract.QContract.contract;
import static team.caltech.olmago.contract.contract.uzoopackage.QUzooPackage.uzooPackage;
import static team.caltech.olmago.contract.discount.QDiscountSubscription.discountSubscription;
import static team.caltech.olmago.contract.plm.QDiscountPolicy.discountPolicy;
import static team.caltech.olmago.contract.product.QProductSubscription.*;

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
  public Long countActiveContractByCustomerAndFeeProductCode(long customerId, String feeProductCode) {
    return jpaQueryFactory
        .select(contract.count())
        .from(contract)
        .where(
            contract.customerId.eq(customerId)
                .and(contract.feeProductCode.eq(feeProductCode))
                .and(contract.lifeCycle.terminationCompletedDateTime.isNotNull())
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
                    .and(uzooPackage.lifeCycle.terminationCompletedDateTime.isNotNull())
                    .and(uzooPackage.lifeCycle.terminationReceivedDateTime.isNotNull())
            )
            .fetchOne()
    );
  }
}
