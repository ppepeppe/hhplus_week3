package kr.hhplus.be.server.apps.product.domain.service;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.common.exception.ProductNotFoundException;
import kr.hhplus.be.server.common.exception.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    public final ProductRepository productRepository;

//    public Product getProductByProductId(long productId) {
//        return productRepository.findProductByProductId(productId)
//                .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND,
//                        "Product not found with ID: " + productId));
//    }
//    /**
//     * 주문 시 상품 수량 차감, 판매량 증가
//     */
//    public Product orderProduct(long productId, Integer quantity) {
//        if (quantity == null || quantity <= 0) {
//            throw new IllegalArgumentException("Order quantity must be greater than 0");
//        }
//        Product product = productRepository.findByIdWithLock(productId);
//        if (product == null) {
//            throw new ProductNotFoundException(ErrorCode.NOT_FOUND_ERROR,
//                    "Product not found with ID: " + productId);
//        }
//        if (product.getQuantity() < quantity) {
//            throw new IllegalArgumentException("재고 부족으로 주문 실패" + productId);
//        }
//        product.setQuantity(product.getQuantity() - quantity);
//        product.setSales(product.getSales() + quantity);
//        return productRepository.save(product);
//    }
//    /**
//     * 상품 리스트 조회
//     */
//    public Page<Product> getProductList(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
//
//        return productRepository.findAll(pageable);
//    }
//    /**
//     * 인기 상품 리스트 조회
//     */
//    public List<Product> getProductListTopN(List<Long> productIds) {
//        return productRepository.findAllById(productIds);
//    }

    public Product getProductByProductId(long productId) {
        return productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with ID: " + productId));
    }

    public Product orderProduct(long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Order quantity must be greater than 0");
        }
        Product product = productRepository.findByIdWithLock(productId);
        if (product == null) {
            throw new ProductNotFoundException(ErrorCode.NOT_FOUND_ERROR,
                    "Product not found with ID: " + productId);
        }
        product.reduceQuantity(quantity); // reduceQuantity 사용
        product.increaseSales(quantity); // increaseSales 사용
        return productRepository.save(product);
    }

    public Page<Product> getProductList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        return productRepository.findAll(pageable);
    }

    public List<Product> getProductListTopN(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }
}
