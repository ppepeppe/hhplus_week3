package kr.hhplus.be.server.apps.coupon.domain.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * 쿠폰조회 (비관적락)
     */
    public Coupon getCouponWithLock(Long couponId) {

        return couponRepository.findCouponByCouponIdWithLock(couponId);
    }
    /**
     * 쿠폰 저장
     */
    public Coupon saveCoupon(Coupon coupon) {
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
        return totalAmount * (coupon.getDiscountPercent());
    }
}
