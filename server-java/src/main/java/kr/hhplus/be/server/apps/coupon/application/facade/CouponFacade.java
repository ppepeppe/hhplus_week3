package kr.hhplus.be.server.apps.coupon.application.facade;

import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.common.util.RedisLockUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponUseCase couponUseCase;
    private final RedissonClient redissonClient;
    private static final String COUPON_LOCK_PREFIX = "coupon:lock:";
    private static final String COUPON_STOCK_KEY = "coupon:stock:";
    private final RedisTemplate<String, Object> redisTemplate;

    public UserCoupon issueCoupon(Long userId, String couponCode, Long couponId) {
        RLock lock = redissonClient.getLock(COUPON_LOCK_PREFIX + couponId);

        try {
            // waitTime과 leaseTime을 적절히 설정
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("Failed to acquire lock");
            }

            return couponUseCase.issueCouponToUser(userId, couponCode, couponId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
