package kr.hhplus.be.server.unit.order;

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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    private static final long ORDER_ID = 1L;
    private static final long PRODUCT_ID = 1L;
    private static final long USER_ID = 1L;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;


    /**
     * 상품 주문의 상품 수량, 상품 item 성공 케이스를 테스트합니다.
     */
    @Test
    @DisplayName("1가지 상품을 주문합니다")
    void shouldOrderProductByProductIdAndQuantitySuccessfully() {
        //given
        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        Order order = Order.builder()
                .orderId(1L)
                .orderId(1L)
                .totalPaymentAmount(100000)
                .totalQuantity(1)
                .build();
        OrderItem orderItem = OrderItem.builder()
                .orderId(ORDER_ID)
                .productId(PRODUCT_ID)
                .paymentAmount(100000)
                .quantity(1)
                .build();
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(PRODUCT_ID)
                .paymentAmount(100000)
                .quantity(1)
                .build();

        orderItemDTOList.add(orderItemDTO);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(new Order(ORDER_ID, USER_ID, 100000, 1));
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(orderItem);

        // when
        Order stubOrder = orderService.order(order, orderItemDTOList);
        // then
        assertThat(stubOrder.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(stubOrder.getUserId()).isEqualTo(USER_ID);
        assertThat(stubOrder.getTotalPaymentAmount()).isEqualTo(100000);
        assertThat(stubOrder.getTotalQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("Order가 null일 때 예외 발생")
    void shouldThrowExceptionWhenOrderIsNull() {
        List<OrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(OrderItemDTO.builder().productId(1L).paymentAmount(1000).quantity(1).build());

        assertThatThrownBy(() -> orderService.order(null, orderItems))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("Order cannot be null");
    }
    @Test
    @DisplayName("OrderItems가 null일 때 예외 발생")
    void shouldThrowExceptionWhenOrderItemsAreNull() {
        Order order = Order.builder().userId(1L).totalPaymentAmount(1000).totalQuantity(1).build();

        assertThatThrownBy(() -> orderService.order(order, null))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("Order items cannot be null or empty");
    }

    @Test
    @DisplayName("OrderItems가 비어 있을 때 예외 발생")
    void shouldThrowExceptionWhenOrderItemsAreEmpty() {
        Order order = Order.builder().userId(1L).totalPaymentAmount(1000).totalQuantity(1).build();
        List<OrderItemDTO> orderItems = new ArrayList<>();

        assertThatThrownBy(() -> orderService.order(order, orderItems))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("Order items cannot be null or empty");
    }
    @Test
    @DisplayName("OrderItemDTO의 Product ID가 유효하지 않을 때 예외 발생")
    void shouldThrowExceptionWhenProductIdIsInvalid() {
        Order order = Order.builder().userId(1L).totalPaymentAmount(1000).totalQuantity(1).build();
        List<OrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(OrderItemDTO.builder().productId(-1L).paymentAmount(1000).quantity(1).build());

        assertThatThrownBy(() -> orderService.order(order, orderItems))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("Product ID must be greater than 0");
    }

    @Test
    @DisplayName("OrderItemDTO의 PaymentAmount가 유효하지 않을 때 예외 발생")
    void shouldThrowExceptionWhenPaymentAmountIsInvalid() {
        Order order = Order.builder().userId(1L).totalPaymentAmount(1000).totalQuantity(1).build();
        List<OrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(OrderItemDTO.builder().productId(1L).paymentAmount(0).quantity(1).build());

        assertThatThrownBy(() -> orderService.order(order, orderItems))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("Payment amount must be greater than 0");
    }

    @Test
    @DisplayName("OrderItemDTO의 Quantity가 유효하지 않을 때 예외 발생")
    void shouldThrowExceptionWhenQuantityIsInvalid() {
        Order order = Order.builder().userId(1L).totalPaymentAmount(1000).totalQuantity(1).build();
        List<OrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(OrderItemDTO.builder().productId(1L).paymentAmount(1000).quantity(0).build());

        assertThatThrownBy(() -> orderService.order(order, orderItems))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("Quantity must be greater than 0");
    }
}
