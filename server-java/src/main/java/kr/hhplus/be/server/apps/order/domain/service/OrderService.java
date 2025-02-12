package kr.hhplus.be.server.apps.order.domain.service;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.apps.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.apps.user.domain.models.entity.User;
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
    /**
     * 주문 상품 정보 생성
     */
    public Order createOrder(User user, List<OrderItem> orderItems, Coupon coupon, double totalAmount) {
        Order order = Order.createOrder(user.getUserId(), orderItems, coupon, (int) totalAmount);
        for (OrderItem orderItem : orderItems) {
            orderItemRepository.save(orderItem);
        }

        // 4. Order에 OrderItems 설정
        return orderRepository.save(order);

    }
}
