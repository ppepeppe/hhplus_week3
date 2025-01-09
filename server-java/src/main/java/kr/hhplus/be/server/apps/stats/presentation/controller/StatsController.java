package kr.hhplus.be.server.apps.stats.presentation.controller;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    @GetMapping("/products/top")
    public List<Product> getProductsByTopList() {
        return List.of(new Product(1L, "패딩", 10000, 10, 0),
                new Product(2L, "신발", 100000, 15, 0));
    }
}
