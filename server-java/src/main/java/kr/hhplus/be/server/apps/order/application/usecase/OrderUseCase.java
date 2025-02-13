package kr.hhplus.be.server.apps.order.application.usecase;

import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import kr.hhplus.be.server.apps.order.domain.models.dto.FinalAmountResult;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderCommand;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderPrepareResult;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.service.OrderService;
import kr.hhplus.be.server.apps.product.domain.service.ProductService;
import kr.hhplus.be.server.apps.stats.domain.service.SalesStatsService;
import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import kr.hhplus.be.server.apps.user.domain.service.UserPointService;
import kr.hhplus.be.server.apps.user.domain.service.UserService;
import kr.hhplus.be.server.apps.mock.DataPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class OrderUseCase {
    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final UserPointService userPointService;
    private final SalesStatsService salesStatsService;
    private final DataPlatformService dataPlatformService;

    @Transactional
    public Order placeOrder(OrderCommand command) {
        // 1. 사용자 검증 및 잔액 확인
        User user = userService.getUserByUserId(command.getUserId());

        // 2. 상품 검증 및 재고 확인
        OrderPrepareResult orderPrepareResult = productService.validateAndPrepareOrderItems(command.getOrderItemDTOList());

        // 3. 쿠폰 적용 로직을 분리해서 처리
        FinalAmountResult finalAmountResult = null;
        if (command.getCouponId() != null) {
            // 3-1. 쿠폰 할인 계산
            finalAmountResult = couponService.useCoupon(command.getCouponId(), orderPrepareResult.getTotalAmount());

            // 3-2. 사용자 쿠폰 사용 처리 (couponService가 아닌 userCouponService에서 처리)
            if (finalAmountResult.getCoupon() != null) {
                userCouponService.useUserCoupon(command.getUserId(), command.getCouponId());
            }
        } else {
            finalAmountResult = FinalAmountResult.builder()
                    .finalAmount(orderPrepareResult.getTotalAmount())
                    .coupon(null)
                    .build();
        }
        // 4. 잔액 차감
        userPointService.deductUserPoint(user.getUserId(), finalAmountResult.getFinalAmount());

        // 5. 주문 생성
        Order order = orderService.createOrder(user, orderPrepareResult.getOrderItems(), finalAmountResult.getCoupon(), finalAmountResult.getFinalAmount());
        // 7. 재고 감소
        productService.decreaseStock(orderPrepareResult.getOrderItems());

        // 8. 판매 통계 업데이트 (Redis + DB)
        salesStatsService.updateSalesStatistics(orderPrepareResult.getOrderItems());

        // 9. 외부 데이터 플랫폼 전송 (비동기)
        dataPlatformService.sendOrderData(order);

        return order;
    }
}