package kr.hhplus.be.server.unit.order.domain;

import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderItemDomainTest {

    @Test
    @DisplayName("유효한 OrderItemDTO로 OrderItem을 생성한다.")
    void shouldCreateOrderItemSuccessfully() {
        // given
        Order order = Order.builder().orderId(1L).build();
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(1L)
                .paymentAmount(1000)
                .quantity(2)
                .build();

        // when
        OrderItem orderItem = OrderItem.fromDTO(order, orderItemDTO);

        // then
        assertThat(orderItem.getOrderId()).isEqualTo(1L);
        assertThat(orderItem.getProductId()).isEqualTo(1L);
        assertThat(orderItem.getPaymentAmount()).isEqualTo(1000);
        assertThat(orderItem.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("OrderItem 생성 시 유효하지 않은 ProductId로 예외 발생")
    void shouldThrowExceptionWhenProductIdIsInvalid() {
        Order order = Order.builder().orderId(1L).build();
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(-1L)
                .paymentAmount(1000)
                .quantity(2)
                .build();

        assertThatThrownBy(() -> OrderItem.fromDTO(order, orderItemDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product ID must be greater than 0");
    }

    @Test
    @DisplayName("OrderItem 생성 시 유효하지 않은 PaymentAmount로 예외 발생")
    void shouldThrowExceptionWhenPaymentAmountIsInvalid() {
        Order order = Order.builder().orderId(1L).build();
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(1L)
                .paymentAmount(0)
                .quantity(2)
                .build();

        assertThatThrownBy(() -> OrderItem.fromDTO(order, orderItemDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment amount must be greater than 0");
    }
}
