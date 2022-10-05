package team.caltech.olmago.contract.domain.contract;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import team.caltech.olmago.contract.domain.exception.InvalidArgumentException;
import team.caltech.olmago.contract.domain.plm.discount.DiscountType;

import java.util.List;
import java.util.Optional;

import static team.caltech.olmago.contract.domain.contract.QContract.contract;
import static team.caltech.olmago.contract.domain.contract.uzoopackage.QUzooPackage.uzooPackage;
import static team.caltech.olmago.contract.domain.discount.QDiscountSubscription.discountSubscription;
import static team.caltech.olmago.contract.domain.product.QProductSubscription.*;
import static team.caltech.olmago.contract.domain.plm.discount.QDiscountPolicy.discountPolicy;
import static team.caltech.olmago.contract.domain.plm.product.QProduct.product;

@Slf4j
@RequiredArgsConstructor
public class ContractRepositoryImpl implements ContractRepositoryCustom {
  private final JPAQueryFactory jpaQueryFactory;
  
  @Override
  public Long countAppliedDcTypeByCustomer(long customerId, DiscountType dcType) {
    return jpaQueryFactory
        .select(contract.count())
        .from(contract)
        .where(contract.customerId.eq(customerId)
                .and(contract.lifeCycle.subscriptionReceivedDateTime.isNotNull())
                .and(contract.lifeCycle.cancelSubscriptionReceiptDateTime.isNull())
                .and(JPAExpressions.selectFrom(productSubscription)
                    .innerJoin(productSubscription.discountSubscriptions, discountSubscription)
                    .innerJoin(discountSubscription.discountPolicy, discountPolicy)
                    .where(productSubscription.contract.eq(contract)
                    .and(discountPolicy.dcType.eq(dcType)))
                    .exists()
                )
        )
        .fetchOne();
  }
  
  @Override
  public Optional<Contract> findWithProductsAndDiscountsById(long contractId) {
    return Optional.ofNullable(
        findByContractId(contractId, false,true).get(0)
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
        .from(contract)
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
        .from(contract)
        .where(
            contract.customerId.eq(customerId)
                .and(includeTerminatedContractExpr(includeTerminatedContract))
        )
        .fetch();
  }
  
  private BooleanExpression includeTerminatedContractExpr(boolean includeTerminatedContract) {
    return includeTerminatedContract ? null : contract.lifeCycle.terminationCompletedDateTime.isNull();
  }
  
  @Override
  public List<Contract> findByContractId(long contractId, boolean withPackageOrOption, boolean includeProductAndDiscount) {
    JPAQuery<Contract> query = jpaQueryFactory.selectFrom(contract);
    if (includeProductAndDiscount) {
      query = appendProductJoin(query);
    }
    /* 맘에 안든다..
        입력으로 들어온 계약id가 같거나 (단품계약용)
        패키지그룹의 옵션계약id가 같으면 패키지계약id까지 같이 조회하거나
        패키지그룹의 패키지계약id가 같으면 옵션계약id까지 같이 조회
     */
    if (withPackageOrOption) {
      return query.where(
          contract.id.eq(contractId)
              .or(contract.id.in(
                      JPAExpressions
                          .select(uzooPackage.packageContract.id)
                          .from(uzooPackage)
                          .where(uzooPackage.optionContract.id.eq(contractId))
                  )
              )
              .or(contract.id.in(
                      JPAExpressions
                          .select(uzooPackage.optionContract.id)
                          .from(uzooPackage)
                          .where(uzooPackage.packageContract.id.eq(contractId))
                  )
              )
      ).distinct().fetch();
    } else {
      return query.where(contract.id.eq(contractId)).distinct().fetch();
    }
  }

  private JPAQuery<Contract> appendProductJoin(JPAQuery<Contract> query) {
    return query
        .join(contract.productSubscriptions, productSubscription).fetchJoin()
        .join(productSubscription.product, product).fetchJoin();
  }
}
