package kr.hhplus.be.server.apps.coupon.domain.service;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.common.exception.CouponNotFoundException;
import kr.hhplus.be.server.common.exception.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * 쿠폰조회 (비관적락)
     */
    public Coupon getCouponWithLock(Long couponId) {
        Coupon coupon = couponRepository.findCouponByCouponIdWithLock(couponId);
        if (coupon == null) {
            throw new CouponNotFoundException(ErrorCode.COUPON_FOUND_ERROR, "Coupon not found with ID: " + couponId);
        }
        return coupon;
    }
    /**
     * 쿠폰 저장
     */
    public Coupon saveCoupon(Coupon coupon) {
        if (coupon == null) {
            throw new IllegalArgumentException("Coupon cannot be null");
        }
        return couponRepository.save(coupon);
    }
    /**
     * 쿠폰조회(락x)
     */
    public Coupon getCouponById(Long couponId) {

        return couponRepository.findCouponByCouponId(couponId);
    }
    /**
     * 쿠펀할인
     */
    public double calculateDiscount(Coupon coupon, double totalAmount) {
        if (coupon == null) {
            throw new IllegalArgumentException("Coupon cannot be null");
        }
        if (totalAmount <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than 0");
        }
        return totalAmount * (coupon.getDiscountPercent());
    }
}
