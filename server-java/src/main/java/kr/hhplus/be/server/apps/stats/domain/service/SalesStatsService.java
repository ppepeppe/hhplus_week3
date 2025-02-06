package kr.hhplus.be.server.apps.stats.domain.service;

import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SalesStatsService {
    private final SalesStatsRepository salesStatsRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "salesStats:";
    /**
     * 최근 N일 동안 가장 많이 팔린 상품 조회 (Redis + DB 혼합 전략)
     */
    public List<Long> getTopSellingProductIds(int days, int topN) {
        LocalDate today = LocalDate.now();
        String redisKey = REDIS_KEY_PREFIX + today.minusDays(days).toString();

        // ✅ Redis에서 조회 (없으면 DB 조회)
        List<Long> cachedData = (List<Long>) redisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            return cachedData;
        }

        // ✅ Redis에 데이터가 없으면 DB에서 조회 후 Redis에 저장
        LocalDate startDate = today.minusDays(days);
        List<Long> topSellingProducts = salesStatsRepository.findTopSellingProductIds(startDate, today, topN);

        // ✅ Redis에 저장 (TTL: 1시간)
        redisTemplate.opsForValue().set(redisKey, topSellingProducts, 6, TimeUnit.HOURS);

        return topSellingProducts;
    }

    /**
     * 주문 발생 시 판매량을 업데이트 (실시간 반영)
     */
    @Transactional
    public void updateSalesStats(Long productId, int quantity) {
        LocalDate today = LocalDate.now();
// ✅ 캐시에 먼저 저장 (Write-Through)
        String redisKey = "salesStats:" + today.toString();
        redisTemplate.opsForHash().increment(redisKey, productId.toString(), quantity);
        salesStatsRepository.updateSalesStats(productId, today, quantity);
    }

}
