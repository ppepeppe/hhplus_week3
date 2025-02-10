package kr.hhplus.be.server.unit.stats;

import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import kr.hhplus.be.server.apps.stats.domain.service.SalesStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static reactor.core.Disposables.never;
@ExtendWith(MockitoExtension.class)
class SalesStatsServiceTest {

    @Mock
    private SalesStatsRepository salesStatsRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private SalesStatsService salesStatsService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    @DisplayName("단일 날짜의 인기 상품 ID를 조회한다")
    void shouldRetrieveSingleDayPopularProducts() {
        // Given
        int days = 1;
        int topN = 3;
        Set<String> mockProductIds = new HashSet<>(Arrays.asList("1", "2", "3"));
        String expectedKey = "popular:" + LocalDate.now();

        when(zSetOperations.reverseRange(anyString(), anyLong(), anyLong()))
                .thenReturn(mockProductIds);

        // When
        List<Long> result = salesStatsService.getPopularProductIds(days, topN);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("여러 날짜의 인기 상품 ID를 합산하여 조회한다")
    void shouldRetrieveMultipleDaysPopularProducts() {
        // Given
        int days = 3;
        int topN = 5;
        Set<String> mockProductIds = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));

        when(zSetOperations.reverseRange(anyString(), anyLong(), anyLong()))
                .thenReturn(mockProductIds);

        // When
        List<Long> result = salesStatsService.getPopularProductIds(days, topN);

        // Then
        assertThat(result).hasSize(5);
        assertThat(result).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    @DisplayName("Redis에서 데이터를 찾지 못할 경우 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoDataFound() {
        // Given
        int days = 1;
        int topN = 3;
        when(zSetOperations.reverseRange(anyString(), anyLong(), anyLong()))
                .thenReturn(null);

        // When
        List<Long> result = salesStatsService.getPopularProductIds(days, topN);

        // Then
        assertThat(result).isEmpty();
    }
    @Test
    @DisplayName("잘못된 형식의 상품 ID가 있을 경우 해당 ID를 제외하고 처리한다")
    void shouldHandleInvalidProductIdFormat() {
        // Given
        int days = 1;
        int topN = 3;
        String expectedKey = "popular:" + LocalDate.now();  // 실제 사용될 키 값
        Set<String> mockProductIds = new HashSet<>(Arrays.asList(
                "1",           // 정상 ID
                "invalid",     // 잘못된 형식
                "3",          // 정상 ID
                "",           // 빈 문자열
                "12.34",      // 소수점
                "-5",         // 음수
                "9999999999999999999"  // overflow 발생 가능한 큰 숫자
        ));

        // 정확한 키와 인덱스로 stubbing
        when(zSetOperations.reverseRange(expectedKey, 0L, 2L))
                .thenReturn(mockProductIds);

        // When
        List<Long> result = salesStatsService.getPopularProductIds(days, topN);
        System.out.println(result);
        // Then
        assertThat(result)
                .isNotNull()
                .hasSize(2)  // 정상적인 ID만 포함되어야 함
                .containsExactly(1L, 3L)  // 정상적인 ID만 순서대로 포함
                .doesNotContain(-5L)  // 음수는 제외
                .allMatch(id -> id > 0);  // 모든 ID는 양수여야 함
    }

}
