package kr.hhplus.be.server.apps.order.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.order.application.usecase.OrderUseCase;
import kr.hhplus.be.server.apps.order.application.usecase.PaymentUseCase;
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

//    @Transactional
//    public Order placeOrder(Long userId, Long couponId, List<OrderItemDTO> orderItems) {
//        Integer totalPaymentAmount = 0;
//        Integer totalQuantity = 0;
//        for (OrderItemDTO orderItem : orderItems) {
//            totalPaymentAmount += orderItem.getPaymentAmount();
//            totalQuantity += orderItem.getQuantity();
//        }
//        Order order = Order.builder()
//                .userId(userId)
//                .totalPaymentAmount(totalPaymentAmount)
//                .totalQuantity(totalQuantity)
//                .build();
//        // 쿠폰 적용 후 가격(없을 시 기존 가격)
//        double totalAmountAfterDiscount = couponUseCase.applyCouponDiscount(userId, couponId, totalPaymentAmount);
//
//        order.setTotalPaymentAmount((int) (totalAmountAfterDiscount));
//        // paymentUsecase 실행(상품, 유저 관련 핸들링)
//        paymentUseCase.handlePayment(order, orderItems);
//        // orderUsecase 실행 (주문 관련 핸들링)
//        orderUseCase.createOrder(order, orderItems);
//        // 외부 플랫폼 전송
////        orderDataSender.sendOrderData(order);
//        return order;
//    }
    @Transactional
    public Order placeOrder(Long userId, Long couponId, List<OrderItemDTO> orderItems) {
        Integer totalPaymentAmount = 0;
        Integer totalQuantity = 0;
        // 총 금액 및 총 수량 계산
        for (OrderItemDTO orderItem : orderItems) {
            totalPaymentAmount += orderItem.getPaymentAmount();
            totalQuantity += orderItem.getQuantity();
        }

        // 주문 생성
        Order order = Order.builder()
                .userId(userId)
                .totalPaymentAmount(totalPaymentAmount)
                .totalQuantity(totalQuantity)
                .build();

        // 쿠폰 적용 후 가격
        double totalAmountAfterDiscount = couponUseCase.applyCouponDiscount(userId, couponId, totalPaymentAmount);
        order.setTotalPaymentAmount((int) totalAmountAfterDiscount);

        // 결제 및 상품 정보 처리
        try {
            paymentUseCase.handlePayment(order, orderItems);
        } catch (RuntimeException e) {
            throw new RuntimeException("주문 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }

        // 주문 저장
        orderUseCase.createOrder(order, orderItems);

        return order;
    }
}
