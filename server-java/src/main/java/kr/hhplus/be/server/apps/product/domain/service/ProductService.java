package kr.hhplus.be.server.apps.product.domain.service;

import jakarta.persistence.OptimisticLockException;
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

    public Product getProductByProductId(long productId) {
        return productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with ID: " + productId));
    }
    /**
     * 주문 시 상품 수량 차감, 판매량 증가
     */
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
    public Product orderProduct(long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Order quantity must be greater than 0");
        }

        // 상품 조회 (낙관적 락은 @Version 필드에서 자동으로 적용됨)
        Product product = productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(ErrorCode.NOT_FOUND_ERROR,
                        "Product not found with ID: " + productId));

        // 재고 확인
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("재고 부족으로 주문 실패: 상품 ID " + productId);
        }

        // 재고 차감 및 판매량 증가
        product.setQuantity(product.getQuantity() - quantity);
        product.setSales(product.getSales() + quantity);

        try {
            return productRepository.save(product); // 낙관적 락 충돌 발생 시 OptimisticLockException 던짐
        } catch (OptimisticLockException e) {
            throw new RuntimeException("상품 업데이트 충돌 발생. 재시도 요청 필요: 상품 ID " + productId, e);
        }
    }
    /**
     * 상품 리스트 조회
     */
    public Page<Product> getProductList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());

        return productRepository.findAll(pageable);
    }
    /**
     * 인기 상품 리스트 조회
     */
    public List<Product> getProductListTopN(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }
}
