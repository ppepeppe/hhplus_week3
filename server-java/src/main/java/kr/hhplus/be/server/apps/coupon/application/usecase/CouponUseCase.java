package kr.hhplus.be.server.apps.coupon.application.usecase;

import org.springframework.transaction.annotation.Transactional;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponUseCase {
    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Transactional
    public void issueCoupon(Long userId, Long couponId) {
        // 1. Redis에서 발급된 쿠폰을 DB에서도 반영
        Coupon coupon = couponService.getCouponById(couponId);
        couponService.incrementCouponUsage(coupon);

        // 2. 사용자 쿠폰 정보 DB 저장
        userCouponService.issueCoupon(userId, couponId);
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
