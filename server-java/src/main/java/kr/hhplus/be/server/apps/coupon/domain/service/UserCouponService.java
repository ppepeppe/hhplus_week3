package kr.hhplus.be.server.apps.coupon.domain.service;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.common.exception.InvalidCouponException;
import kr.hhplus.be.server.common.exception.UserCouponNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;
    /**
     * 쿠폰발급
     */
    public UserCoupon issueCoupon(Long userId, Long couponId) {
        if (userId == null || couponId == null) {
            throw new IllegalArgumentException("User ID and Coupon ID cannot be null.");
        }
        UserCoupon userCoupon = UserCoupon.builder()
                .couponId(couponId)
                .userId(userId)
                .isUsed(false)
                .build();

        try {
            return userCouponRepository.save(userCoupon);
        } catch (Exception e) {
            throw new InvalidCouponException("Failed to issue coupon to user. Please try again.");
        }
    }
    /**
     * 유저쿠폰 조회
     */
    public Optional<UserCoupon> getUserCouponByUserIdAndCouponId(Long userId, Long couponId) {
        return Optional.ofNullable(userCouponRepository.findUserCouponByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new UserCouponNotFoundException("UserCoupon not found for User ID: " + userId + ", Coupon ID: " + couponId)));
    }
    /**
     * 유저쿠폰 사용여부
     */
    public void markCouponAsUsed(UserCoupon userCoupon) {
        if (userCoupon.getIsUsed()) {
            throw new InvalidCouponException("The coupon has already been used.");
        }
        userCoupon.setIsUsed(true);
        userCouponRepository.save(userCoupon);
    }
    /**
     * 사용자가 갖고있는 쿠폰 조회
     */
    public List<UserCoupon> getUserCouponListByUserId(Long userId) {

        return userCouponRepository.findAllByUserId(userId);
    }

}
