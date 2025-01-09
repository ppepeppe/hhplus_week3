package kr.hhplus.be.server.apps.product.domain.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
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

        return productRepository.findProductByProductId(productId);
    }
    /**
     * 주문 시 상품 수량 차감, 판매량 증가
     */
    @Transactional
    public Product orderProduct(long productId, Integer quantity) {

        Product product = productRepository.findByIdWithLock(productId);
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("재고 부족으로 주문 실패");
        }
        product.setQuantity(product.getQuantity() - quantity);
        product.setSales(product.getSales() + quantity);
        return productRepository.save(product);
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
