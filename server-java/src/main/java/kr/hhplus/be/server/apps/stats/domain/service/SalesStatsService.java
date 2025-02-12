package kr.hhplus.be.server.apps.stats.domain.service;

import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.service.ProductService;
import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesStatsService {
    private final RedisTemplate<String, String> redisTemplate;



    private static final String POPULAR_KEY_PREFIX = "popular:";
    private static final String UNION_KEY_PREFIX = "popular:union:";

    /**
     * 지난 N일 동안의 인기 상품 ID 조회
     */
    public List<Long> getPopularProductIds(int days, int topN) {
        List<String> keys = generatePopularKeys(days);
        String unionKey = createUnionKey(keys, days);

        List<Long> popularProductIds = getProductsFromRedisKey(unionKey, topN);

        // 생성된 임시 키 삭제
        if (keys.size() > 1) {
            redisTemplate.delete(unionKey);
        }

        return popularProductIds;
    }

    /**
     * Redis에서 특정 날짜의 인기 상품 ID 조회
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
     * 최근 N일 동안의 Redis 키 리스트 생성
     */
    private List<String> generatePopularKeys(int days) {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            keys.add(POPULAR_KEY_PREFIX + LocalDate.now().minusDays(i));
        }
        return keys;
    }


    /**
     * 여러 날짜의 인기 상품 데이터를 합산하는 Redis 키 생성
     */
    private String createUnionKey(List<String> keys, int days) {
        if (keys.size() == 1) {
            return keys.get(0);
        }
        String unionKey = UNION_KEY_PREFIX + days;
        redisTemplate.opsForZSet().unionAndStore(keys.get(0), keys.subList(1, keys.size()), unionKey);
        return unionKey;
    }

    /**
     * 주문 아이템들의 판매 통계 업데이트
     */
    public void updateSalesStatistics(List<OrderItem> orderItems) {
        String todayKey = POPULAR_KEY_PREFIX + LocalDate.now();

        try {
            // 주문 아이템들의 판매량을 Redis에 업데이트
            for (OrderItem orderItem : orderItems) {
                redisTemplate.opsForZSet().incrementScore(
                        todayKey,
                        orderItem.getProductId().toString(),
                        orderItem.getQuantity()
                );
            }

            // 키 만료시간 설정 (3일)
            if (Boolean.FALSE.equals(redisTemplate.hasKey(todayKey))) {
                redisTemplate.expire(todayKey, 3, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            log.error("Failed to update sales statistics in Redis for orderItems: {}", orderItems, e);
        }
    }

}
