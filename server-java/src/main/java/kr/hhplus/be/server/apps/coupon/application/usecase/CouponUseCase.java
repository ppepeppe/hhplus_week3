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
        // 쿠폰을 비관적 락으로 조회
        Coupon coupon = couponService.getCouponWithLock(couponId);

        // 발급 가능 여부 확인
        if (coupon.getCurrentCount() >= coupon.getMaxCount()) {
            throw new IllegalArgumentException("쿠폰 수량 부족");
        }

        // 쿠폰 발급 수 증가
        coupon.setCurrentCount(coupon.getCurrentCount() + 1);
        // 변경된 쿠폰 저장
        couponService.saveCoupon(coupon);

        // UserCoupon 발급
        return userCouponService.issueCoupon(userId, couponId);
    }

    public double applyCouponDiscount(Long userId, Long couponId, double totalAmount) {
        // 사용 가능한 유저 쿠폰 조회
//        UserCoupon userCoupon = userCouponService.getUserCouponByUserId(userId);
//        if (userCoupon == null) {
//            return totalAmount;
//        }
        // 쿠폰 정보 조회
        Coupon coupon = couponService.getCouponById(couponId);
        if (coupon == null) {
            return totalAmount;
        }
        // 할인 계산
        double discount = couponService.calculateDiscount(coupon, totalAmount);
        Optional<UserCoupon> userCoupon = userCouponService.getUserCouponByUserIdAndCouponId(userId, couponId);
        // 쿠폰 사용 처리
        userCouponService.markCouponAsUsed(userCoupon.get());

        // 할인 금액 반환
        return totalAmount - discount;
    }
    public List<UserCoupon> getUserCouponListByUserId(Long userId) {
        return userCouponService.getUserCouponListByUserId(userId);
    }

    public Coupon registerCoupon(String code, Double discountPercent, LocalDate validDate, Integer maxCount, Integer currentCount) {
        Coupon coupon = Coupon.builder()
                .code(code)
                .discountPercent(discountPercent)
                .validDate(validDate)
                .maxCount(30)
                .currentCount(0)
                .build();

        return couponService.saveCoupon(coupon);
    }

}
