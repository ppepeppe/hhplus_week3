package kr.hhplus.be.server.apps.stats.domain.repository;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatsRepository {
    List<Long> findTopSellingProductIds(LocalDate startDate, int topN);
}
