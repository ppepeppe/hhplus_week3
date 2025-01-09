package kr.hhplus.be.server.apps.order.application.usecase;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderDto;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.service.OrderService;
import kr.hhplus.be.server.apps.payment.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderUseCase {
    private final OrderService orderService;
    private final PaymentService paymentService;
    // 주문 생성
    @Transactional
    public Order createOrder(OrderDto orderDto, List<OrderItemDTO> orderItems) {

        return orderService.order(orderDto, orderItems);
    }
}