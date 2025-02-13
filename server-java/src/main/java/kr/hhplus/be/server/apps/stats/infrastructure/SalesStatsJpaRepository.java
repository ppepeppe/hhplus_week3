package kr.hhplus.be.server.apps.stats.infrastructure;

import kr.hhplus.be.server.apps.stats.domain.models.SalesStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatsJpaRepository extends JpaRepository<SalesStats, Long> {
    /**
     * 특정 기간 동안 가장 많이 팔린 상품 ID 조회 (TOP N)
     */
    @Query(value = "SELECT s.product_id " +
            "FROM sales_stats s " +
            "WHERE s.sold_date BETWEEN :startDate AND :endDate " +
            "GROUP BY s.product_id " +
            "ORDER BY SUM(s.sold_quantity) DESC " +
            "LIMIT :topN", nativeQuery = true)
    List<Long> findTopSellingProductIds(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("topN") int topN
    );

    /**
     * 주문이 발생하면 해당 상품의 판매량을 실시간 업데이트 (실시간 집계)
     * 기존 데이터가 있으면 sold_quantity 증가, 없으면 새로운 데이터 삽입
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO sales_stats (product_id, sold_date, sold_quantity) " +
            "VALUES (:productId, :soldDate, :quantity) " +
            "ON DUPLICATE KEY UPDATE sold_quantity = sold_quantity + VALUES(sold_quantity)",
            nativeQuery = true)
    void updateSalesStats(
            @Param("productId") Long productId,
            @Param("soldDate") LocalDate soldDate,
            @Param("quantity") int quantity
    );

}


