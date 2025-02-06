package kr.hhplus.be.server.unit.order.domain;

import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderDomainTest {

    @Test
    @DisplayName("유효한 Order와 OrderItems로 총 금액과 총 수량을 계산한다.")
    void shouldCalculateTotalPaymentAmountAndQuantitySuccessfully() {
        // given
        List<OrderItemDTO> orderItems = List.of(
                OrderItemDTO.builder().productId(1L).paymentAmount(1000).quantity(2).build(),
                OrderItemDTO.builder().productId(2L).paymentAmount(2000).quantity(1).build()
        );

        // when
        Order order = Order.createOrder(1L, orderItems);

        // then
        assertThat(order.getTotalPaymentAmount()).isEqualTo(3000);
        assertThat(order.getTotalQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("Order 생성 시 null OrderItems로 예외 발생")
    void shouldThrowExceptionWhenOrderItemsAreNull() {
        assertThatThrownBy(() -> Order.createOrder(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order items cannot be null or empty");
    }

    @Test
    @DisplayName("Order 생성 시 빈 OrderItems로 예외 발생")
    void shouldThrowExceptionWhenOrderItemsAreEmpty() {
        assertThatThrownBy(() -> Order.createOrder(1L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order items cannot be null or empty");
    }
}
