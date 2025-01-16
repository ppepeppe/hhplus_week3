package kr.hhplus.be.server.apps.coupon.infrastructure;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
    UserCoupon save(UserCoupon userCoupon);
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long  couponId);
    List<UserCoupon> findAllByUserId(Long userId);

}
