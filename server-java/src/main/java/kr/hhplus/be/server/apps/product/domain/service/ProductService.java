package kr.hhplus.be.server.apps.product.domain.service;

import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderPrepareResult;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    public final ProductRepository productRepository;
    /**
     * 상품 조회
     */
    public Product getProductByProductId(long productId) {
        return productRepository.findProductByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with ID: " + productId));
    }

    /**
     * 상품 주문 시 수량 차감 및 판매량 증가 (비관적 락 대신 분산 락으로 보호되는 로직)
     */
    @Transactional
    public Product orderProduct(long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Order quantity must be greater than 0");
        }
        // 기존의 findByIdWithLock() 대신 일반 조회
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new ProductNotFoundException(ErrorCode.NOT_FOUND_ERROR,
                        "Product not found with ID: " + productId));
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("재고 부족으로 주문 실패: 상품 ID " + productId);
        }
        product.reduceQuantity(quantity);
        product.increaseSales(quantity);
        return productRepository.save(product);
    }
    /**
     * 전체 상품 리스트 조회
     */
    public Page<Product> getProductList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        return productRepository.findAll(pageable);
    }

    public List<Product> getProductListTopN(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    @Transactional
    public OrderPrepareResult validateAndPrepareOrderItems(List<OrderItemDTO> items) {
        List<OrderItem> orderItems = new ArrayList<>();
        Integer totalAmount = 0;

        for (OrderItemDTO itemReq : items) {
            Product product = productRepository.findByIdWithLock(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            Integer itemAmount = product.getPrice() * itemReq.getQuantity();
            totalAmount += itemAmount;

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getProductId())
                    .quantity(itemReq.getQuantity())
                    .paymentAmount(itemAmount)
                    .build();

            orderItems.add(orderItem);
        }

        return new OrderPrepareResult(orderItems, totalAmount);
    }

    public void decreaseStock(List<OrderItem> orderItems) {
        orderItems.forEach(item -> {
            Long productId = item.getProductId();
            Product product = productRepository.findProductByProductId(productId).orElseThrow();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            System.out.println(product);
            productRepository.save(product);

        });
    }


}
