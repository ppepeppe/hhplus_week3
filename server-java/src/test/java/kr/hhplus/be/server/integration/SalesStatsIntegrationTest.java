package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.apps.stats.application.SalesStatsUseCase;
import kr.hhplus.be.server.apps.stats.domain.models.SalesStats;
import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SalesStatsIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SalesStatsUseCase salesStatsUseCase;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SalesStatsRepository salesStatsRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @BeforeEach
    void setUp() {
        // Product 초기화
        Product productA = new Product(null, "Product A", 1000, 10, 0);
        Product productB = new Product(null, "Product B", 1500, 40, 25);
        Product productC = new Product(null, "Product C", 2000, 30, 20);
        Product productD = new Product(null, "Product D", 3000, 20, 15);
        Product productE = new Product(null, "Product E", 2500, 10, 5);
        Product productG = new Product(null, "Product G", 2500, 10, 5);
        productRepository.saveAll(List.of(productA, productB, productC, productD, productE, productG));

        SalesStats stats1 = new SalesStats(null, 1L, 10, LocalDate.now().minusDays(2));
        SalesStats stats2 = new SalesStats(null, 1L, 5, LocalDate.now().minusDays(1));
        SalesStats stats3 = new SalesStats(null, 2L, 8, LocalDate.now().minusDays(3));
        SalesStats stats4 = new SalesStats(null, 2L, 12, LocalDate.now().minusDays(1));
        SalesStats stats5 = new SalesStats(null, 3L, 15, LocalDate.now().minusDays(2));
        SalesStats stats6 = new SalesStats(null, 4L, 6, LocalDate.now().minusDays(3));
        SalesStats stats7 = new SalesStats(null, 6L, 30, LocalDate.now().minusDays(5));
        SalesStats stats8 = new SalesStats(null, 5L, 4, LocalDate.now().minusDays(1));
        salesStatsRepository.saveAll(List.of(stats1, stats2, stats3, stats4, stats5, stats6, stats7, stats8));

        // 🔹 Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // 🔹 Redis에 인기 상품 데이터 삽입
        addMockDataToRedis("popular:" + LocalDate.now().toString(), "1", 15);
        addMockDataToRedis("popular:" + LocalDate.now().minusDays(1), "2", 12);
        addMockDataToRedis("popular:" + LocalDate.now().minusDays(1), "3", 20);
        addMockDataToRedis("popular:" + LocalDate.now().minusDays(2), "4", 8);
        addMockDataToRedis("popular:" + LocalDate.now().minusDays(3), "5", 6);

    }
    @Test
    @DisplayName("판매량 top 5 성공 테스트")
    public void testGetProducts() {
        // given
        int topN = 5;
        int days = 5;

        // when
        List<Product> products = salesStatsUseCase.getProductsTopN(days, topN);

        // then
        assertNotNull(products);
        assertThat(products).hasSize(5);

        // ✅ 정렬된 순서대로 특정 이름이 있는지 체크
        assertThat(products).extracting("name").containsExactly("Product A", "Product B", "Product C", "Product D", "Product E");
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 삭제
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("TRUNCATE TABLE sales_stats");
        redisTemplate.getConnectionFactory().getConnection().flushAll();

    }


    /**
     * 🔹 Redis에 테스트용 데이터 추가
     */
    private void addMockDataToRedis(String key, String productId, double score) {
        redisTemplate.opsForZSet().add(key, productId, score);
    }
}
