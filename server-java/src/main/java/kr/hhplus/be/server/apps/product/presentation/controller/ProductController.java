package kr.hhplus.be.server.apps.product.presentation.controller;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    /**
     * 상품id로 상품 조회
     */
    @GetMapping("/{productId}")
    public Product getProductByProductId(@PathVariable("productId") long productId) {
        return productService.getProductByProductId(productId);
    }
    /**
     * 상품 리스트 조회
     */
    @GetMapping("/all")
    public Page<Product> getProductList(@RequestParam(defaultValue = "0") int page, // 기본 페이지 번호: 0
                                        @RequestParam(defaultValue = "10") int size ) {
        return productService.getProductList(page, size);
    }

}
