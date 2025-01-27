package kr.hhplus.be.server.apps.order.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.order.application.usecase.OrderUseCase;
import kr.hhplus.be.server.apps.order.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.mock.OrderDataSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderUseCase orderUseCase;
    private final PaymentUseCase paymentUseCase;
    private final OrderDataSender orderDataSender;
    private final CouponUseCase couponUseCase;

    @Transactional
    public Order placeOrder(Long userId, Long couponId, List<OrderItemDTO> orderItems) {
        System.out.println(orderItems);
        // 주문 생성
        Order order = Order.createOrder(userId, orderItems);

        // 쿠폰 할인 적용
        double discountedAmount = couponUseCase.applyCouponDiscount(userId, couponId, order.getTotalPaymentAmount());
        order.applyDiscount(discountedAmount);

        // 주문 항목 생성
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderItemDTO orderItemDto : orderItems) {
            orderItemList.add(OrderItem.fromDTO(order, orderItemDto));
        }
        paymentUseCase.handlePayment(order, orderItemList);
        // 주문 저장
        orderUseCase.createOrder(order, orderItemList);

        return order;
    }
}
