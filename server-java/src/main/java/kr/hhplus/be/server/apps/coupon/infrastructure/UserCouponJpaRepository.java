package kr.hhplus.be.server.apps.coupon.infrastructure;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
    UserCoupon save(UserCoupon userCoupon);
    UserCoupon findByUserId(Long userId);
}
