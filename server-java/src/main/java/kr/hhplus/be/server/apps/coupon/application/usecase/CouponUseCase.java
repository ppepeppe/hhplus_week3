package kr.hhplus.be.server.apps.coupon.application.usecase;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import kr.hhplus.be.server.apps.user.domain.service.UserService;
import kr.hhplus.be.server.common.util.LockService;
import kr.hhplus.be.server.common.util.RedissonLockService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceTransformer;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CouponUseCase {
    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COUPON_STOCK_KEY = "coupon:stock:";

    private final LockService lockService;

    @Transactional
    public UserCoupon execute(Long userId, String couponCode, Long couponId) {
        Coupon coupon = couponService.getCouponById(couponId);
        couponService.incrementCouponUsage(coupon);
        redisTemplate.opsForValue().decrement(COUPON_STOCK_KEY + couponId);
        return userCouponService.issueCoupon(userId, couponId);
    }

    public List<UserCoupon> getUserCouponListByUserId(Long userId) {
        return userCouponService.getUserCouponListByUserId(userId);
    }

    public Coupon registerCoupon(String code, Double discountPercent, LocalDate validDate, Integer maxCount, Integer currentCount) {
        Coupon coupon = Coupon.create(code, discountPercent, validDate, maxCount, currentCount); // 팩토리 메서드 호출
        return couponService.saveCoupon(coupon);
    }
}
