package kr.hhplus.be.server.apps.stats.presentation.controller;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.stats.application.SalesStatsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {
    private final SalesStatsUseCase salesStatsUseCase;
    @GetMapping("/products/top")
    public List<Product> getProductsByTopList() {

        return salesStatsUseCase.getProductsTopN(LocalDate.now(), 5);
    }
}
