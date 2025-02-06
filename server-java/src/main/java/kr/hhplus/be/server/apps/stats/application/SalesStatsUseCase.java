package kr.hhplus.be.server.apps.stats.application;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.service.ProductService;
import kr.hhplus.be.server.apps.stats.domain.service.SalesStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SalesStatsUseCase {
    private final SalesStatsService salesStatsService;
    private final ProductService productService;
    // 인기 상품 조회
    public List<Product> getProductsTopN(int days, int topN) {
        // 날짜 기준 인기 상품 조회
        List<Long> productIdList = salesStatsService.getTopSellingProductIds(days, topN);
        // 인기 상품 IdList로 상품 조회
        return productService.getProductListTopN(productIdList);
    }
}
