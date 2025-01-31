package kr.hhplus.be.server.apps.coupon.domain.service;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.common.exception.CouponNotFoundException;
import kr.hhplus.be.server.common.exception.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * 쿠폰조회 (비관적락)
     */
    public Coupon getCouponWithLock(Long couponId) {

        return couponRepository.findCouponByCouponIdWithLock(couponId)
                .orElseThrow(() -> new CouponNotFoundException(ErrorCode.COUPON_FOUND_ERROR, "Coupon not found with ID: " + couponId));
    }

    public void incrementCouponUsage(Coupon coupon) {
        coupon.incrementUsage(); // 도메인 객체 메서드 호출
        couponRepository.save(coupon);
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
//
//    public Coupon getCouponById(Long couponId) {
//        return couponRepository.findCouponByCouponId(couponId)
//                .orElseThrow(() -> new CouponNotFoundException(ErrorCode.COUPON_FOUND_ERROR, "Coupon not found with ID: " + couponId));
//    }
    public Optional<Coupon> getCouponById(Long couponId) {
        return couponRepository.findCouponByCouponId(couponId);
    }
    /**
     * 쿠펀할인
     */
    public double calculateDiscount(Coupon coupon, double totalAmount) {
        return coupon.calculateDiscount(totalAmount, coupon.getDiscountPercent()); // 도메인 객체 메서드 호출
    }

}
