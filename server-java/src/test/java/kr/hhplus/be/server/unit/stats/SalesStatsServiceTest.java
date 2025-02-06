package kr.hhplus.be.server.unit.stats;

import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import kr.hhplus.be.server.apps.stats.domain.service.SalesStatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesStatsServiceTest {
    @InjectMocks
    private SalesStatsService salesStatsService;

    @Mock
    private SalesStatsRepository salesStatsRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;  // Redis 값을 다루는 인터페이스 모의(Mock) 객체 추가

    @Test
    @DisplayName("날짜 기준 3일간 제일 많이 팔린 5개의 상품을 조회합니다.")
    void shouldRetrieveTop5ProductIdSuccessfully() {
        // given
        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L);
        String redisKey = "salesStats:" + LocalDate.now().minusDays(3);

        // ✅ RedisTemplate의 opsForValue() 메서드 동작을 모의(Mock)
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(null); // 캐시에 값이 없다고 가정
        when(salesStatsRepository.findTopSellingProductIds(LocalDate.now().minusDays(3), LocalDate.now(), 5))
                .thenReturn(ids);

        // when
        List<Long> idList = salesStatsService.getTopSellingProductIds(3, 5);

        // then
        assertThat(idList).isEqualTo(ids);
    }

    @Test
    @DisplayName("Redis에 캐시된 데이터가 있을 경우, DB를 조회하지 않고 캐시 데이터를 반환한다.")
    void shouldReturnCachedDataIfAvailable() {
        // given
        List<Long> cachedIds = List.of(1L, 2L, 3L, 4L, 5L);
        String redisKey = "salesStats:" + LocalDate.now().minusDays(3);

        // ✅ Redis에 데이터가 있다고 가정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(cachedIds); // 캐시 데이터 설정

        // when
        List<Long> idList = salesStatsService.getTopSellingProductIds(3, 5);

        // then
        assertThat(idList).isEqualTo(cachedIds);
    }
}
