package kr.hhplus.be.server.apps.coupon.domain.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;
    /**
     * 쿠폰발급
     */
    @Transactional
    public UserCoupon issueCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = UserCoupon.builder()
                .couponId(couponId)
                .userId(userId)
                .isUsed(false)
                .build();

        return userCouponRepository.save(userCoupon);
    }
    /**
     * 유저쿠폰 조회
     */
    public UserCoupon getUserCouponByUserId(Long userId) {
        return userCouponRepository.findUserCouponByUserId(userId);
    }
    /**
     * 유저쿠폰 사용여부
     */
    public void markCouponAsUsed(UserCoupon userCoupon) {
        userCoupon.setIsUsed(true);
        userCouponRepository.save(userCoupon);
    }
}
