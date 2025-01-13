package kr.hhplus.be.server.apps.order.domain.service;

import kr.hhplus.be.server.apps.order.domain.models.dto.OrderDto;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.apps.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    public final OrderRepository orderRepository;
    public final OrderItemRepository orderItemRepository;
    /**
     * 주문 상품 정보 저장
     */
    public Order order(Order order, List<OrderItemDTO> orderItems) {
        // 주문 정보 저장
        Order newOrder = orderRepository.save(order);
        // 주문 item 저장
        for (OrderItemDTO orderItemDto : orderItems) {
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getOrderId())
                    .productId(orderItemDto.getProductId())
                    .paymentAmount(orderItemDto.getPaymentAmount())
                    .quantity(orderItemDto.getQuantity())
                    .build();
            orderItemRepository.save(orderItem);
        }

        return newOrder;
    }
}
