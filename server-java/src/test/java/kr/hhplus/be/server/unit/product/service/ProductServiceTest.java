package kr.hhplus.be.server.unit.product.service;

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

    @Test
    @DisplayName("상품ID로 해당 상품을 조회한다.")
    void shouldRetrieveProductByIdSuccessfully() {
        // Given
        when(productRepository.findProductByProductId(PRODUCT_ID)).thenReturn(Optional.of(new Product(PRODUCT_ID, "패딩", 100000, 10, 0)));

        // When
        Product product = productService.getProductByProductId(PRODUCT_ID);

        // Then
        assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getName()).isEqualTo("패딩");
        assertThat(product.getPrice()).isEqualTo(100000);
        assertThat(product.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("상품ID로 조회 시 존재하지 않는 경우 예외 발생")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findProductByProductId(PRODUCT_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductByProductId(PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with ID: " + PRODUCT_ID);
    }

    @Test
    @DisplayName("상품ID로 재고와 판매량을 업데이트한다.")
    void shouldReduceStockAndIncreaseSalesSuccessfully() {
        // Given
        Product product = new Product(PRODUCT_ID, "패딩", 100000, 10, 0);
        when(productRepository.findByIdWithLock(PRODUCT_ID)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 9, 1));

        // When
        Product updatedProduct = productService.orderProduct(PRODUCT_ID, 1);

        // Then
        assertThat(updatedProduct.getQuantity()).isEqualTo(9);
        assertThat(updatedProduct.getSales()).isEqualTo(1);
    }

    @Test
    @DisplayName("상품 목록을 페이징하여 조회한다.")
    void shouldRetrieveProductListSuccessfully() {
        // Given
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

        Pageable pageable = PageRequest.of(0, 2, Sort.by("productId").ascending());
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<Product> result = productService.getProductList(0, 2);

        // Then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0)).isEqualTo(product1);
        assertThat(result.getContent().get(1)).isEqualTo(product2);
    }

}
