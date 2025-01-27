package kr.hhplus.be.server.apps.coupon.application.usecase;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponUseCase {
    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Transactional
    public UserCoupon issueCoupon(Long userId, Long couponId) {
        Coupon coupon = couponService.getCouponWithLock(couponId);
        couponService.incrementCouponUsage(coupon);
        return userCouponService.issueCoupon(userId, couponId);
    }
    public double applyCouponDiscount(Long userId, Long couponId, double totalAmount) {
        Coupon coupon = couponService.getCouponById(couponId).orElse(null);
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
