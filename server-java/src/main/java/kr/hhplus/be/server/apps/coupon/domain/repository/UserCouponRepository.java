package kr.hhplus.be.server.apps.coupon.domain.repository;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository {
    UserCoupon save(UserCoupon userCoupon);
    List<UserCoupon> findAllByUserId(Long userId);
    Optional<UserCoupon> findUserCouponByUserIdAndCouponId(Long userId, Long couponId);
}