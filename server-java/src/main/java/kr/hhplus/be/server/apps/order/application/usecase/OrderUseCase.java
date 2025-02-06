package kr.hhplus.be.server.apps.order.application.usecase;

import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderUseCase {
    private final OrderService orderService;
    // 주문 생성
    public void createOrder(Order order, List<OrderItem> orderItems) {
        orderService.saveOrder(order, orderItems);
    }
}