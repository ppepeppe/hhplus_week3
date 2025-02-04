package kr.hhplus.be.server.apps.coupon.application.usecase;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import kr.hhplus.be.server.common.util.RedisLockUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponUseCase {
    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserCoupon couponIssuanceTransactional(Long userId, Long couponId) {
        Coupon coupon = couponService.getCouponById(couponId);
        couponService.incrementCouponUsage(coupon);
        return userCouponService.issueCoupon(userId, couponId);
    }
    /**
     * 쿠폰 할인 적용
     */

    public double applyCouponDiscount(Long userId, Long couponId, double totalAmount) {
        Coupon coupon = couponService.getCouponById(couponId);
        if (coupon == null) {
            return totalAmount;
        }
        double discount = coupon.calculateDiscount(totalAmount, coupon.getDiscountPercent()); // 도메인 객체 메서드 호출
        userCouponService.useUserCoupon(userId, couponId); // 상태 변경
        return totalAmount - discount;
    }

    public List<UserCoupon> getUserCouponListByUserId(Long userId) {
        return userCouponService.getUserCouponListByUserId(userId);
    }

    public Coupon registerCoupon(String code, Double discountPercent, LocalDate validDate, Integer maxCount, Integer currentCount) {
        Coupon coupon = Coupon.create(code, discountPercent, validDate, maxCount, currentCount); // 팩토리 메서드 호출
        return couponService.saveCoupon(coupon);
    }
}
