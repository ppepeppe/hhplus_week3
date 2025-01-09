package kr.hhplus.be.server.apps.coupon.infrastructure;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon findCouponByCouponId(long couponId) {
        return couponJpaRepository.findCouponByCouponId(couponId);
    }
    @Override
    public Coupon findCouponByCouponIdWithLock(Long couponId) {
        return couponJpaRepository.findCouponByCouponIdWithLock(couponId);
    }
    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }
}
