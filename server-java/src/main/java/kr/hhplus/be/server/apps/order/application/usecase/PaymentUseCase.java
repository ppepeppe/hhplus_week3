package kr.hhplus.be.server.apps.order.application.usecase;

import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.product.domain.service.ProductService;
import kr.hhplus.be.server.apps.stats.domain.service.SalesStatsService;
import kr.hhplus.be.server.apps.user.domain.service.UserPointService;
import kr.hhplus.be.server.common.util.RedisLockUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentUseCase {
    private final UserPointService userPointService;
    private final ProductService productService;
    private final SalesStatsService salesStatsService;
    private final RedisLockUtil redisLockUtil;

//    public void handlePayment(Order order, List<OrderItem> orderItems) {
//        // 포인트 차감
//        userPointService.orderUserPoint(order.getUserId(), order.getTotalPaymentAmount());
//
//        // 상품 정보 업데이트
//        for (OrderItem orderItem : orderItems) {
//            productService.orderProduct(orderItem.getProductId(), orderItem.getQuantity());
//        }
//    }
    public void handlePayment(Order order, List<OrderItem> orderItems) {
        // 1. 포인트 차감 (사용자 포인트 처리)
        userPointService.orderUserPoint(order.getUserId(), order.getTotalPaymentAmount());

        // 2. 상품 정보 업데이트 (각 주문 항목별로 재고 차감)
        for (OrderItem orderItem : orderItems) {
            String lockKey = "lock:product:" + orderItem.getProductId();
            // 사용자 ID를 락 식별값으로 사용 (또는 다른 고유값)
            boolean acquired = redisLockUtil.acquireLock(lockKey, order.getUserId(), 3, 10);
            if (!acquired) {
                throw new RuntimeException("상품 주문 요청이 많아 처리되지 않았습니다. 다시 시도해주세요.");
            }
            try {
                // 판매량 업데이트 (sales_stats 테이블 반영)
//                salesStatsService.updateSalesStats(orderItem.getProductId(), orderItem.getQuantity());

                productService.orderProduct(orderItem.getProductId(), orderItem.getQuantity());
            } finally {
                redisLockUtil.releaseLock(lockKey, order.getUserId());
            }
        }
    }
}
