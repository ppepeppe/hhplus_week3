package kr.hhplus.be.server.apps.coupon.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.common.util.RedisLockUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponUseCase couponUseCase;
    private final RedisLockUtil redisLockUtil;
    public UserCoupon issueCoupon(Long userId, Long couponId) {
        String lockKey = generateLockKey(couponId);
        boolean acquired = redisLockUtil.acquireLock(lockKey, userId, 3, 10);
        if (!acquired) {
            throw new RuntimeException("쿠폰 발급 요청이 많아 처리되지 않았습니다. 다시 시도해주세요.");
        }
        try {
            return couponUseCase.couponIssuanceTransactional(userId, couponId);
        } finally {
            redisLockUtil.releaseLock(lockKey, userId);
        }
    }

    private String generateLockKey(Long couponId) {
        return "lock:coupon:" + couponId;
    }
}
