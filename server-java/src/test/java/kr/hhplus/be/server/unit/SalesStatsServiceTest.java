package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import kr.hhplus.be.server.apps.stats.domain.service.SalesStatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    /**
     * 최근 3일 판매량 top5 상품 조회의 성공케이스를 테스트합니다.
     */
    @Test
    @DisplayName("날짜 기준 3일간 제일 많이 팔린 5개의 상품을 조회합니다.")
    void shouldRetrieveTop5ProductIdSuccessfully() {
        List<Long> ids = List.of(1L ,2L ,3L ,4L ,5L);

        when(salesStatsRepository.findTopSellingProductIds(LocalDate.of(2025, 1, 6), 5))
                .thenReturn(ids);
        // when
        List<Long> idList = salesStatsService.getTopSellingProductIds(LocalDate.of(2025, 1, 9), 5);

        // then
        assertThat(idList).isEqualTo(ids);
    }

}
