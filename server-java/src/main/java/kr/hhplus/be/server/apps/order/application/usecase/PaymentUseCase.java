package kr.hhplus.be.server.apps.order.application.usecase;

import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
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
    private final UserCouponService userCouponService;

//    public void handlePayment(Order order, List<OrderItemDTO> orderItemDTOList) {
//        // 주문시 포인트 변경
//        userPointService.orderUserPoint(order.getUserId(), order.getTotalPaymentAmount());
//        // 주문시 상품정보 변경
//        for (OrderItemDTO orderItemDTO : orderItemDTOList) {
//            productService.orderProduct(orderItemDTO.getProductId(), orderItemDTO.getQuantity());
//        }
//    }
    public void handlePayment(Order order, List<OrderItemDTO> orderItemDTOList) {
        // 주문 시 포인트 차감
        userPointService.orderUserPoint(order.getUserId(), order.getTotalPaymentAmount());

        // 주문 시 상품 정보 변경
        for (OrderItemDTO orderItemDTO : orderItemDTOList) {
            try {
                productService.orderProduct(orderItemDTO.getProductId(), orderItemDTO.getQuantity());
            } catch (RuntimeException e) {
                throw new RuntimeException("상품 정보 업데이트 중 문제가 발생했습니다: " + e.getMessage(), e);
            }
        }
    }
}
