package kr.hhplus.be.server.unit.product.domain;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductDomainTest {

    @Test
    @DisplayName("재고 감소가 정상적으로 작동한다.")
    void shouldReduceQuantitySuccessfully() {
        // Given
        Product product = Product.builder()
                .productId(1L)
                .name("패딩")
                .price(100000)
                .quantity(10)
                .sales(0)
                .build();

        // When
        product.reduceQuantity(5);

        // Then
        assertThat(product.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("재고가 부족할 경우 예외를 발생시킨다.")
    void shouldThrowExceptionWhenReducingMoreThanAvailable() {
        // Given
        Product product = Product.builder()
                .productId(1L)
                .name("패딩")
                .price(100000)
                .quantity(5)
                .sales(0)
                .build();

        // When & Then
        assertThatThrownBy(() -> product.reduceQuantity(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 부족");
    }

    @Test
    @DisplayName("판매량 증가가 정상적으로 작동한다.")
    void shouldIncreaseSalesSuccessfully() {
        // Given
        Product product = Product.builder()
                .productId(1L)
                .name("패딩")
                .price(100000)
                .quantity(10)
                .sales(0)
                .build();

        // When
        product.increaseSales(5);

        // Then
        assertThat(product.getSales()).isEqualTo(5);
    }
}
