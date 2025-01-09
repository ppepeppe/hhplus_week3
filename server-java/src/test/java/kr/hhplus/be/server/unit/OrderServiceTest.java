package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.apps.order.domain.models.dto.OrderDto;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.apps.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.apps.order.domain.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        OrderDto orderDto = OrderDto.builder()
                .userId(USER_ID)
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
        Order order = orderService.order(orderDto, orderItemDTOList);
        System.out.println(order);
        // then
        assertThat(order.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(order.getUserId()).isEqualTo(USER_ID);
        assertThat(order.getTotalPaymentAmount()).isEqualTo(100000);
        assertThat(order.getTotalQuantity()).isEqualTo(1);
    }

}
