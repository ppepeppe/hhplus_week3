package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.stats.application.SalesStatsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class SalesStatsIntegrationTest {

    @Autowired
    private SalesStatsUseCase salesStatsUseCase;
    @Test
    public void testGetProducts() {
        // given
        LocalDate nowDate = LocalDate.now();
        int topN = 5;

        // when
        List<Product> products = salesStatsUseCase.getProductsTopN(nowDate, topN);

        // then
        assertNotNull(products);
        assertThat(products.size()).isEqualTo(topN);
        assertThat(products.get(0).getName()).isEqualTo("Product A");
        assertThat(products.get(1).getName()).isEqualTo("Product B");
        assertThat(products.get(2).getName()).isEqualTo("Product C");
        assertThat(products.get(3).getName()).isEqualTo("Product D");
        assertThat(products.get(4).getName()).isEqualTo("Product E");
    }
}