package kr.hhplus.be.server.apps.order.application.facade;

import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.order.application.usecase.OrderUseCase;
import kr.hhplus.be.server.apps.order.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderDto;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.mock.OrderDataSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderUseCase orderUseCase;
    private final PaymentUseCase paymentUseCase;
    private final OrderDataSender orderDataSender;
    private final CouponUseCase couponUseCase;

    public Order orderPlace(OrderDto orderDto, List<OrderItemDTO> orderItems) {
        // 쿠폰 적용 후 가격(없을 시 기존 가격)
        double totalAmountAfterDiscount = couponUseCase.applyCouponDiscount(orderDto.getUserId(), orderDto.getTotalPaymentAmount());

        orderDto.setTotalPaymentAmount((int) (totalAmountAfterDiscount));
        // paymentUsecase 실행(상품, 유저 관련 핸들링)
        paymentUseCase.handlePayment(orderDto, orderItems);
        // orderUsecase 실행 (주문 관련 핸들링)
        Order order = orderUseCase.createOrder(orderDto, orderItems);
        // 외부 플랫폼 전송
        orderDataSender.sendOrderData(order);
        return order;
    }
}
