package kr.hhplus.be.server.api.stats.controller;

import kr.hhplus.be.server.domain.product.models.Products;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    @GetMapping("/products/top")
    public List<Products> getProductsByTopList() {
        return List.of(new Products(1L, "패딩", 10000, 10),
                new Products(2L, "신발", 100000, 15));
    }
}
