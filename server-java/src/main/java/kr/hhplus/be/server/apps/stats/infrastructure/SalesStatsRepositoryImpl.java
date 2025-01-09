package kr.hhplus.be.server.apps.stats.infrastructure;

import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SalesStatsRepositoryImpl implements SalesStatsRepository {
    private final SalesStatsJpaRepository salesStatsJpaRepository;
    @Override
    public List<Long> findTopSellingProductIds(LocalDate startDate, int topN) {
        return salesStatsJpaRepository.findTopSellingProductIds(startDate, topN);
    }
}
