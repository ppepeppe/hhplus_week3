package kr.hhplus.be.server.apps.coupon.domain.service;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.common.exception.CouponNotFoundException;
import kr.hhplus.be.server.common.exception.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public Integer getCouponStock(Long couponId) {
        String couponStockKey = "coupon:stock:" + couponId;
        return(Integer) redisTemplate.opsForValue().get(couponStockKey);
    }
    /**
     * 쿠폰을 Redis에 저장 (쿠폰 등록)
     */
    public void createCoupon(Long couponId, int stock, double discount, int expiryHours) {
        String couponStockKey = "coupon:stock:" + couponId;
        String couponInfoKey = "coupon:info:" + couponId;

        // 1. 쿠폰 개수 저장 (TTL 적용)
        redisTemplate.opsForValue().set(couponStockKey, stock);
        redisTemplate.expire(couponStockKey, expiryHours, TimeUnit.HOURS);

        // 2. 쿠폰 정보 저장 (HashMap 형태)
        Map<String, Object> couponInfo = new HashMap<>();
        couponInfo.put("couponId", couponId);
        couponInfo.put("discount", discount);
        couponInfo.put("stock", stock);
        couponInfo.put("expiryHours", expiryHours);

        redisTemplate.opsForHash().putAll(couponInfoKey, couponInfo);
        redisTemplate.expire(couponInfoKey, expiryHours, TimeUnit.HOURS);
    }
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
    public Coupon getCouponById(Long couponId) {

        return couponRepository.findCouponByCouponId(couponId).orElse(null);
    }
    /**
     * 쿠폰 ID에 해당하는 쿠폰의 초기 재고를 Redis에 등록합니다.
     *
     * @param couponId 쿠폰 식별자
     * @param stock    초기 재고 수량
     */
    public void registerCoupon(Long couponId, int stock) {
        String key = "coupon:stock:" + couponId;
        // Redis에 문자열 형태로 초기 재고를 저장 (예: "30")
        redisTemplate.opsForValue().set(key, stock);
    }
}
