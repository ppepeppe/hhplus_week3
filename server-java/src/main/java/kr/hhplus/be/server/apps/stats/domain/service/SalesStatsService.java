package kr.hhplus.be.server.apps.stats.domain.service;

import io.lettuce.core.RedisException;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesStatsService {
    private final RedisTemplate<String, String> redisTemplate;
    private final SalesStatsRepository salesStatsRepository;



    private static final String POPULAR_KEY_PREFIX = "popular:";
    private static final String UNION_KEY_PREFIX = "popular:union:";

    public List<Long> getPopularProductIds(int days, int topN) {
        try {
            // 1. Redis에서 조회 시도
            List<String> keys = generatePopularKeys(days);
            String unionKey = createUnionKey(keys, days);
            List<Long> popularProductIds = getProductsFromRedisKey(unionKey, topN);

            // 생성된 임시 키 삭제
            if (keys.size() > 1) {
                redisTemplate.delete(unionKey);
            }

            // Redis에 데이터가 없는 경우 DB 조회
            if (popularProductIds.isEmpty()) {
                return getPopularProductIdsFromDB(days, topN);
            }

            return popularProductIds;

        } catch (RedisConnectionFailureException | RedisException e) {
            log.warn("Redis operation failed, falling back to database", e);
            return getPopularProductIdsFromDB(days, topN);
        }
    }

    private List<Long> getPopularProductIdsFromDB(int days, int topN) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        return salesStatsRepository.findTopSellingProductIds(startDate, endDate, topN);
    }

    /**
     * Key 값으로 레디스 데이터를 가져옵니다.
     * @param key
     * @param topN
     */
    private List<Long> getProductsFromRedisKey(String key, int topN) {
        Set<String> productIds = redisTemplate.opsForZSet().reverseRange(key, 0, topN - 1);

        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIdList = new ArrayList<>();
        for (String idStr : productIds) {
            try {
                if (Long.parseLong(idStr) > 0) {
                    productIdList.add(Long.parseLong(idStr));
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid product ID format: {}", idStr, e);
            }
        }

        return productIdList;
    }

    /**
     *
     * 일자간의 레디스 키를 생성합니다.
     * @param days
     */
    private List<String> generatePopularKeys(int days) {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            keys.add(POPULAR_KEY_PREFIX + LocalDate.now().minusDays(i));
        }
        return keys;
    }

    /**
     * n일간의 레디스 합산 키를 생성합니다/
     *
     * @param keys
     * @param days
     */
    private String createUnionKey(List<String> keys, int days) {
        if (keys.size() == 1) {
            return keys.get(0);
        }
        String unionKey = UNION_KEY_PREFIX + days;
        redisTemplate.opsForZSet().unionAndStore(keys.get(0), keys.subList(1, keys.size()), unionKey);
        return unionKey;
    }
//
//    /**
//     * 주문이 일어나면 해당일의 판매량을 업데이트 합니다.
//     * @param orderItems
//     */
//    public void updateSalesStatistics(List<OrderItem> orderItems) {
//        String todayKey = POPULAR_KEY_PREFIX + LocalDate.now();
//
//        try {
//            // 주문 아이템들의 판매량을 Redis에 업데이트
//            for (OrderItem orderItem : orderItems) {
//                redisTemplate.opsForZSet().incrementScore(
//                        todayKey,
//                        orderItem.getProductId().toString(),
//                        orderItem.getQuantity()
//                );
//            }
//
//            // 키 만료시간 설정 (3일)
//            if (Boolean.FALSE.equals(redisTemplate.hasKey(todayKey))) {
//                redisTemplate.expire(todayKey, 3, TimeUnit.DAYS);
//            }
//        } catch (Exception e) {
//            log.error("Failed to update sales statistics in Redis for orderItems: {}", orderItems, e);
//        }
//    }

    @Transactional
    public void updateSalesStatistics(List<OrderItem> orderItems) {
        String todayKey = POPULAR_KEY_PREFIX + LocalDate.now();
        LocalDate today = LocalDate.now();

        // 1. Redis 업데이트 시도
        try {
            for (OrderItem orderItem : orderItems) {
                redisTemplate.opsForZSet().incrementScore(
                        todayKey,
                        orderItem.getProductId().toString(),
                        orderItem.getQuantity()
                );
            }

            // Redis 키 만료시간 설정 (3일)
            if (Boolean.FALSE.equals(redisTemplate.hasKey(todayKey))) {
                redisTemplate.expire(todayKey, 3, TimeUnit.DAYS);
            }
        } catch (RedisConnectionFailureException | RedisException e) {
            log.error("Failed to update Redis statistics", e);
            // Redis 실패는 무시하고 계속 진행 (DB 업데이트는 필수)
        }

        // 2. DB 업데이트 (SalesStats 테이블)
        try {
            for (OrderItem orderItem : orderItems) {
                // UPSERT 방식으로 통계 업데이트
                salesStatsRepository.updateSalesStats(
                        orderItem.getProductId(),
                        today,
                        orderItem.getQuantity()
                );
            }
        } catch (Exception e) {
            log.error("Failed to update DB statistics", e);
            throw new RuntimeException("Failed to update sales statistics", e);
        }
    }

}
