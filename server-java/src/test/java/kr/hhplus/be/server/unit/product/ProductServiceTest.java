package kr.hhplus.be.server.unit.product;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.apps.product.domain.service.ProductService;
import kr.hhplus.be.server.common.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private static final long PRODUCT_ID = 1L;
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    /**
     * 사용자 포인트 조회의 성공 케이스를 테스트합니다.
     */
    @Test
    @DisplayName("상품ID로 해당 상품을 조회한다.")
    void shouldRetrieveProductByUserIdSuccessfully() {
        // given
        when(productRepository.findProductByProductId(PRODUCT_ID)).thenReturn(Optional.of(new Product(PRODUCT_ID, "패딩", 100000, 10, 0)));
        // when
        Product product = productService.getProductByProductId(PRODUCT_ID);

        // then
        assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getName()).isEqualTo("패딩");
        assertThat(product.getPrice()).isEqualTo(100000);
        assertThat(product.getQuantity()).isEqualTo(10);

    }
    /**
     * 상품ID가 없는 상품을 죄회합니다
     */
    @Test
    @DisplayName("Product ID로 조회 시 존재하지 않는 경우 예외 발생")
    void shouldThrowExceptionWhenProductNotFound() {
        Long invalidProductId = 999L; // 존재하지 않는 상품 ID
        when(productRepository.findProductByProductId(invalidProductId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductByProductId(invalidProductId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with ID: " + invalidProductId);
    }
    /**
     * 재고 부족시 예외처리를 테스트합니다
     */
    @Test
    @DisplayName("재고 부족으로 주문 실패 시 예외 발생")
    void shouldThrowExceptionWhenStockInsufficient() {
        Long productId = 1L;
        int orderQuantity = 10;

        Product product = new Product(productId, "Test Product", 1000, 5, 0);
        when(productRepository.findByIdWithLock(productId)).thenReturn(product);

        assertThatThrownBy(() -> productService.orderProduct(productId, orderQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고 부족으로 주문 실패" + productId);
    }
    /**
     * 0 또는 음수의 수량 주문시 예외처리합니다.
     */
    @Test
    @DisplayName("잘못된 주문 수량으로 주문 실패 시 예외 발생")
    void shouldThrowExceptionWhenOrderQuantityInvalid() {
        assertThatThrownBy(() -> productService.orderProduct(1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order quantity must be greater than 0");

        assertThatThrownBy(() -> productService.orderProduct(1L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order quantity must be greater than 0");
    }
    /**
     * 상품 ID로 상품 재고 차감의 성공 케이스를 테스트 합니다.
     */
    @Test
    @DisplayName("상품ID로 해당 상품의 재고를 차감한다.")
    void shouldReduceProductStockSuccessfully() {
        // given
        when(productRepository.findByIdWithLock(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 10, 0));
        when(productRepository.save(any(Product.class))).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 9, 0));
        // when
        Product product = productService.orderProduct(PRODUCT_ID, 1);

        // then
        assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getName()).isEqualTo("패딩");
        assertThat(product.getPrice()).isEqualTo(100000);
        assertThat(product.getQuantity()).isEqualTo(9);

    }
    /**
     * 상품 ID로 판매량 증가의 성공 케이스를 테스트 합니다.
     */
    @Test
    @DisplayName("상품ID로 해당 상품의 판매량을 증가시킨다.")
    void shouldIncreaseProductStockSuccessfully() {
        // given
        when(productRepository.findByIdWithLock(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 10, 0));
        when(productRepository.save(new Product(PRODUCT_ID, "패딩", 100000, 9, 1))).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 9, 1));
        // when
        Product product = productService.orderProduct(PRODUCT_ID, 1);

        // then
        assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getName()).isEqualTo("패딩");
        assertThat(product.getPrice()).isEqualTo(100000);
        assertThat(product.getQuantity()).isEqualTo(9);
        assertThat(product.getSales()).isEqualTo(1);
    }

    /**
     * 상품의 목록을 조회하는 성공 케이스를 테스트합니다.
     */
    @Test
    @DisplayName("상품목록을 조회한다.")
    void shouldRetrieveProductListSuccessfully() {
        Product product1 = Product.builder()
                .productId(1L)
                .name("패딩")
                .price(200000)
                .quantity(10)
                .sales(0)
                .build();
        Product product2 = Product.builder()
                .productId(2L)
                .name("신발")
                .price(100000)
                .quantity(10)
                .sales(0)
                .build();
        List<Product> productList = List.of(product1, product2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("productId").ascending()); // page: 0, size: 2
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // when
        Page<Product> result = productService.getProductList(0, 2);
        // then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0)).isEqualTo(product1);
        assertThat(result.getContent().get(1)).isEqualTo(product2);

    }

}
