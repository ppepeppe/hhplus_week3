package kr.hhplus.be.server.apps.stats.infrastructure;

import kr.hhplus.be.server.apps.stats.domain.models.SalesStats;
import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SalesStatsRepositoryImpl implements SalesStatsRepository {
    private final SalesStatsJpaRepository salesStatsJpaRepository;
    @Override
    public List<Long> findTopSellingProductIds(LocalDate startDate, LocalDate endDate, int topN) {
        return salesStatsJpaRepository.findTopSellingProductIds(startDate, endDate, topN);
    }

    @Override
    public SalesStats save(SalesStats salesStats) {
        return salesStatsJpaRepository.save(salesStats);
    }

    @Override
    public void saveAll(List<SalesStats> salesStatsList) {
        salesStatsJpaRepository.saveAll(salesStatsList);
    }

    @Override
    public void updateSalesStats(Long productId, LocalDate soldDate, int quantity) {
        salesStatsJpaRepository.updateSalesStats(productId, soldDate, quantity);
    }
}
