package kr.hhplus.be.server.apps.stats.infrastructure;

import kr.hhplus.be.server.apps.stats.domain.models.SalesStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatsJpaRepository extends JpaRepository<SalesStats, Long> {
    @Query(value = "SELECT s.product_id " +
            "FROM sales_stats s " +
            "WHERE s.sold_date >= :startDate " +
            "GROUP BY s.product_id " +
            "ORDER BY SUM(s.sold_quantity) DESC " +
            "LIMIT :topN", nativeQuery = true)
    List<Long> findTopSellingProductIds(@Param("startDate") LocalDate startDate, @Param("topN") int topN);
}


