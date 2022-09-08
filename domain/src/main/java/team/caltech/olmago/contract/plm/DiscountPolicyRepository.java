package team.caltech.olmago.contract.plm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, String> {
  @Query(
      "SELECT dp " +
      "FROM DiscountPolicy dp " +
      "WHERE couponPolicyCode = :couponPolicyCode "
  )
  Optional<DiscountPolicy> findByCouponPolicyCode(@Param("couponPolicyCode") String couponPolicyCode);
}
