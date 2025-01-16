package kr.hhplus.be.server.apps.coupon.domain.repository;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository {
    Coupon findCouponByCouponId(long couponId);
    Coupon findCouponByCouponIdWithLock(@Param("couponId") Long couponId);
    Coupon save(Coupon coupon);
}
