package kr.hhplus.be.server.apps.order.domain.service;

import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.apps.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.common.exception.OrderException;
import kr.hhplus.be.server.common.exception.vo.ErrorCode;
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
//    public Order order(Order order, List<OrderItemDTO> orderItems) {
//        if (order == null) {
//            throw new OrderException(ErrorCode.BAD_REQUEST_ERROR, "Order cannot be null");
//        }
//        if (orderItems == null || orderItems.isEmpty()) {
//            throw new OrderException(ErrorCode.BAD_REQUEST_ERROR, "Order items cannot be null or empty");
//        }
//        // 주문 정보 저장
//        Order newOrder = orderRepository.save(order);
//        // 주문 item 저장
//        for (OrderItemDTO orderItemDto : orderItems) {
//            if (orderItemDto.getProductId() == null || orderItemDto.getProductId() <= 0) {
//                throw new OrderException(ErrorCode.BAD_REQUEST_ERROR,
//                        "Product ID must be greater than 0: " + orderItemDto.getProductId());
//            }
//            if (orderItemDto.getPaymentAmount() <= 0) {
//                throw new OrderException(ErrorCode.BAD_REQUEST_ERROR,
//                        "Payment amount must be greater than 0: " + orderItemDto.getPaymentAmount());
//            }
//            if (orderItemDto.getQuantity() <= 0) {
//                throw new OrderException(ErrorCode.BAD_REQUEST_ERROR,
//                        "Quantity must be greater than 0: " + orderItemDto.getQuantity());
//            }
//
//            OrderItem orderItem = OrderItem.builder()
//                    .orderId(order.getOrderId())
//                    .productId(orderItemDto.getProductId())
//                    .paymentAmount(orderItemDto.getPaymentAmount())
//                    .quantity(orderItemDto.getQuantity())
//                    .build();
//            orderItemRepository.save(orderItem);
//        }
//
//        return newOrder;
//    }
    public Order saveOrder(Order order, List<OrderItem> orderItems) {
        if (order == null) {
            throw new OrderException(ErrorCode.BAD_REQUEST_ERROR, "Order cannot be null");
        }
        if (orderItems == null) {
            throw new OrderException(ErrorCode.BAD_REQUEST_ERROR, "Order items cannot be null or empty");
        }

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 주문 항목 저장
        for (OrderItem orderItem : orderItems) {
            orderItemRepository.save(orderItem);
        }

        return savedOrder;
    }
}
