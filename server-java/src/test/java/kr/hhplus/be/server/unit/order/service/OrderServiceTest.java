package kr.hhplus.be.server.unit.order.service;

import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.apps.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.apps.order.domain.service.OrderService;
import kr.hhplus.be.server.common.exception.OrderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 정보와 주문 항목을 성공적으로 저장한다.")
    void shouldSaveOrderAndOrderItemsSuccessfully() {
        // given
        Order order = Order.builder().userId(1L).totalPaymentAmount(1000).totalQuantity(2).build();
        OrderItem orderItem = OrderItem.builder()
                .productId(1L)
                .paymentAmount(1000)
                .quantity(2)
                .build();
        List<OrderItem> orderItems = List.of(orderItem);

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(OrderItem.builder().build());

        // when
        Order savedOrder = orderService.saveOrder(order, orderItems);

        // then
        assertThat(savedOrder.getTotalPaymentAmount()).isEqualTo(1000);
        assertThat(savedOrder.getTotalQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Order가 null일 때 예외 발생")
    void shouldThrowExceptionWhenOrderIsNull() {
        List<OrderItem> orderItems = List.of(
                OrderItem.builder().productId(1L).paymentAmount(1000).quantity(2).build()
        );

        assertThatThrownBy(() -> orderService.saveOrder(null, orderItems))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("Order cannot be null");
    }

    @Test
    @DisplayName("OrderItems가 null일 때 예외 발생")
    void shouldThrowExceptionWhenOrderItemsAreNull() {
        Order order = Order.builder().userId(1L).totalPaymentAmount(1000).totalQuantity(1).build();

        assertThatThrownBy(() -> orderService.saveOrder(order, null))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("Order items cannot be null or empty");
    }
}
