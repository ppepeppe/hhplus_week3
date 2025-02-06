package kr.hhplus.be.server.apps.coupon.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.order.application.usecase.OrderUseCase;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.common.util.RedisLockUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponUseCase couponUseCase;
    private final RedisLockUtil redisLockUtil;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
//    public UserCoupon issueCoupon(Long userId, Long couponId) {
//        String lockKey = generateLockKey(couponId);
//        boolean acquired = redisLockUtil.acquireLock(lockKey, userId, 3, 10);
//        if (!acquired) {
//            throw new RuntimeException("쿠폰 발급 요청이 많아 처리되지 않았습니다. 다시 시도해주세요.");
//        }
//        try {
//            return couponUseCase.couponIssuanceTransactional(userId, couponId);
//        } finally {
//            redisLockUtil.releaseLock(lockKey, userId);
//        }
//    }

    public void issueCoupon(Long userId, Long couponId) throws InterruptedException {
        String couponStockKey = "coupon:stock:" + couponId;
        String issuedUsersKey = "coupon:issued:users:" + couponId;
        RLock lock = redissonClient.getLock("lock:coupon:" + couponId);

        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new RuntimeException("현재 쿠폰 발급 요청이 많습니다. 다시 시도해주세요.");
            }

            // 쿠폰 정보 확인 (쿠폰이 Redis에 있는지 체크)
            if (!redisTemplate.hasKey(couponStockKey)) {
                throw new RuntimeException("유효하지 않은 쿠폰입니다.");
            }

            // 이미 쿠폰을 받은 사용자인지 확인
            if (redisTemplate.opsForSet().isMember(issuedUsersKey, userId)) {
                throw new RuntimeException("이미 쿠폰을 발급받았습니다.");
            }

            // 쿠폰 개수 차감
            Long currentStock = redisTemplate.opsForValue().increment(couponStockKey, -1);
            if (currentStock < 0) {
                redisTemplate.opsForValue().increment(couponStockKey, 1);
                throw new RuntimeException("쿠폰이 모두 소진되었습니다.");
            }
            // 쿠폰 발급 정보를 UseCase에 전달하여 DB에도 반영
            couponUseCase.issueCoupon(userId, couponId);
            // 선착순 쿠폰 발급 처리
            redisTemplate.opsForSet().add(issuedUsersKey, userId);

        } finally {
            lock.unlock();
        }
    }
    private String generateLockKey(Long couponId) {
        return "lock:coupon:" + couponId;
    }
}
