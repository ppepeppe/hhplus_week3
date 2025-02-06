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
    public void issueCoupon(Long userId, Long couponId) {
        validateIds(userId, couponId); // 유효성 검사 분리
        UserCoupon userCoupon = UserCoupon.create(userId, couponId); // 객체 생성 로직 분리

        saveUserCoupon(userCoupon); // 저장 로직 분리
    }
    private void saveUserCoupon(UserCoupon userCoupon) {
        try {
            userCouponRepository.save(userCoupon);
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
     * 사용자가 갖고있는 쿠폰 조회
     */
    public List<UserCoupon> getUserCouponListByUserId(Long userId) {

        return userCouponRepository.findAllByUserId(userId);
    }
    /**
     * 유저쿠폰 사용여부
     */
    public void useUserCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = userCouponRepository.findUserCouponByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new UserCouponNotFoundException("UserCoupon not found for User ID: " + userId + ", Coupon ID: " + couponId));
        userCoupon.markAsUsed(); // 도메인 객체 메서드 호출
        userCouponRepository.save(userCoupon);
    }
    public void validateIds(Long userId, Long couponId) {
        if (userId == null || couponId == null) {
            throw new IllegalArgumentException("User ID and Coupon ID cannot be null.");
        }
    }

}
