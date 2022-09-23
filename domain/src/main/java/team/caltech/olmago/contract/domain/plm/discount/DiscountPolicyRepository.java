package team.caltech.olmago.contract.domain.plm.discount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, String> {
  Optional<DiscountPolicy> findByCouponPolicyCode(@Param("couponPolicyCode") String couponPolicyCode);
}
