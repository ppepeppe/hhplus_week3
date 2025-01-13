package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.order.application.facade.OrderFacade;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderDto;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testOrderPlaceWithCoupon() {
        // given 주문 데이터 준비
        Order order = Order.builder()
                .userId(1L)
                .totalPaymentAmount(1000)
                .totalQuantity(1)
                .build();
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(1L)
                .paymentAmount(1000)
                .quantity(1)
                .build();
        List<OrderItemDTO> orderItems = List.of(orderItemDTO);

        // When: OrderFacade를 호출
        Order order1 = orderFacade.placeOrder(1L, 1L, orderItems);

        // Then: 결과 검증
        assertNotNull(order1);
        assertEquals(750, order1.getTotalPaymentAmount()); // 할인 금액 검증
    }
}
