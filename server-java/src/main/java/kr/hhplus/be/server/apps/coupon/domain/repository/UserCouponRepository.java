package kr.hhplus.be.server.apps.coupon.domain.repository;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository {
    UserCoupon save(UserCoupon userCoupon);
    UserCoupon findUserCouponByUserId(Long userId);
}
