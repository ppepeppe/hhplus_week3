package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.apps.stats.application.SalesStatsUseCase;
import kr.hhplus.be.server.apps.stats.domain.models.SalesStats;
import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    }
    @Test
    public void testGetProducts() {
        // given
        LocalDate nowDate = LocalDate.now();
        int topN = 5;

        // when
        List<Product> products = salesStatsUseCase.getProductsTopN(nowDate, topN);
        System.out.println(products);
        // then
        assertNotNull(products);
        assertThat(products.size()).isEqualTo(topN);
        assertThat(products.get(0).getName()).isEqualTo("Product A");
        assertThat(products.get(1).getName()).isEqualTo("Product B");
        assertThat(products.get(2).getName()).isEqualTo("Product C");
        assertThat(products.get(3).getName()).isEqualTo("Product D");
        assertThat(products.get(4).getName()).isEqualTo("Product E");
    }
    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 삭제
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("TRUNCATE TABLE sales_stats");
    }
}
