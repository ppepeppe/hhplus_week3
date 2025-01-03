package kr.hhplus.be.server.api.product.controller;

import kr.hhplus.be.server.domain.product.models.Products;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
    @GetMapping("/{productId}")
    public Products getProductByProductId(@PathVariable("productId") long productId) {
        return new Products(productId, "패딩", 10000, 10);
    }

}
