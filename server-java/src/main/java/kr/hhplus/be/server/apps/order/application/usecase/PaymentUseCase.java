package kr.hhplus.be.server.apps.order.application.usecase;

import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.product.domain.service.ProductService;
import kr.hhplus.be.server.apps.user.domain.service.UserPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentUseCase {
    private final UserPointService userPointService;
    private final ProductService productService;

    public void handlePayment(Order order, List<OrderItem> orderItems) {
        // 포인트 차감
        userPointService.orderUserPoint(order.getUserId(), order.getTotalPaymentAmount());

        // 상품 정보 업데이트
        for (OrderItem orderItem : orderItems) {
            productService.orderProduct(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

}
