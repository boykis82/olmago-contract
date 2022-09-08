package team.caltech.olmago.contract.contract.uzoopackage;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.caltech.olmago.contract.contract.Contract;

import java.util.Optional;

import static team.caltech.olmago.contract.contract.uzoopackage.QUzooPackage.uzooPackage;

/*
example
  member.username.eq("member1") // username = 'member1'
  member.username.ne("member1") //username != 'member1'
  member.username.eq("member1").not() // username != 'member1'
  member.username.isNotNull() //이름이 is not null
  member.age.in(10, 20) // age in (10,20)
  member.age.notIn(10, 20) // age not in (10, 20)
  member.age.between(10,30) //between 10, 30
  member.age.goe(30) // age >= 30
  member.age.gt(30) // age > 30
  member.age.loe(30) // age <= 30
  member.age.lt(30) // age < 30
  member.username.like("member%") //like 검색
  member.username.contains("member") // like ‘%member%’ 검색
  member.username.startsWith("member") //like ‘member%’ 검색
 */

@RequiredArgsConstructor
public class UzooPackageRepositoryImpl implements UzooPackageRepositoryCustom {
  private final JPAQueryFactory jpaQueryFactory;
  
  public Optional<UzooPackage> findActivePackage(Contract pkgContract, Contract optContract) {
    return Optional.ofNullable(
        jpaQueryFactory.select(uzooPackage)
            .from(uzooPackage)
            .where(uzooPackage.packageContract.eq(pkgContract)
                    .and(uzooPackage.optionContract.eq(optContract))
                    .and(uzooPackage.lifeCycle.terminationCompletedDateTime.isNull())
                    .and(uzooPackage.lifeCycle.terminationReceivedDateTime.isNull())
                    .and(uzooPackage.lifeCycle.subscriptionReceivedDateTime.isNotNull())
                    .and(uzooPackage.lifeCycle.subscriptionCompletedDateTime.isNotNull())
            )
            .fetchOne()
    );
  }
  
  public Optional<UzooPackage> findSubscriptionReceivedPackage(Contract contract) {
    return Optional.ofNullable(
        jpaQueryFactory.select(uzooPackage)
            .from(uzooPackage)
            .where(
                packageOrOptionContract(contract)
                    .and(uzooPackage.lifeCycle.terminationCompletedDateTime.isNull())
                    .and(uzooPackage.lifeCycle.terminationReceivedDateTime.isNull())
                    .and(uzooPackage.lifeCycle.subscriptionReceivedDateTime.isNotNull())
                    .and(uzooPackage.lifeCycle.subscriptionCompletedDateTime.isNull())
                )
            .fetchOne()
    );
  }

  public Optional<UzooPackage> findTerminationReceivedPackage(Contract pkgContract, Contract optContract) {
    return Optional.ofNullable(
        jpaQueryFactory.select(uzooPackage)
            .from(uzooPackage)
            .where(uzooPackage.packageContract.eq(pkgContract)
                .and(uzooPackage.optionContract.eq(optContract))
                .and(uzooPackage.lifeCycle.terminationCompletedDateTime.isNull())
                .and(uzooPackage.lifeCycle.terminationReceivedDateTime.isNotNull())
                .and(uzooPackage.lifeCycle.subscriptionReceivedDateTime.isNotNull())
                .and(uzooPackage.lifeCycle.subscriptionCompletedDateTime.isNotNull())
            )
            .fetchOne()
    );
  }
  
  public Optional<UzooPackage> findTerminationReceivedPackage(Contract contract) {
    return Optional.ofNullable(
        jpaQueryFactory.select(uzooPackage)
            .from(uzooPackage)
            .where(
                packageOrOptionContract(contract)
                    .and(uzooPackage.lifeCycle.terminationCompletedDateTime.isNull())
                    .and(uzooPackage.lifeCycle.terminationReceivedDateTime.isNotNull())
                    .and(uzooPackage.lifeCycle.subscriptionReceivedDateTime.isNotNull())
                    .and(uzooPackage.lifeCycle.subscriptionCompletedDateTime.isNotNull())
            )
            .fetchOne()
    );
  }
  
  private BooleanExpression packageOrOptionContract(Contract contract) {
    return uzooPackage.packageContract.eq(contract).or(uzooPackage.optionContract.eq(contract));
  }
  
}
