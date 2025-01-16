package kr.hhplus.be.server.apps.stats.domain.repository;

import kr.hhplus.be.server.apps.stats.domain.models.SalesStats;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatsRepository {
    List<Long> findTopSellingProductIds(LocalDate startDate, int topN);
    SalesStats save(SalesStats salesStats);
    void saveAll(List<SalesStats> salesStatsList);
}
