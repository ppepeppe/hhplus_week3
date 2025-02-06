package kr.hhplus.be.server.apps.coupon.domain.repository;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository {
    Optional<Coupon> findCouponByCouponId(long couponId);
    Optional<Coupon> findCouponByCouponIdWithLock(@Param("couponId") Long couponId);
    Coupon save(Coupon coupon);

}
