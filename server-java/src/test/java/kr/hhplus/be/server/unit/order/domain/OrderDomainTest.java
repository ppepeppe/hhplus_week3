package kr.hhplus.be.server.unit.order.domain;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderDomainTest {
    @Test
    @DisplayName("유효한 주문 생성 - 쿠폰 적용")
    void createOrderWithCoupon() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .paymentAmount(1000)
                        .quantity(2)
                        .build(),
                OrderItem.builder()
                        .productId(2L)
                        .paymentAmount(2000)
                        .quantity(1)
                        .build()
        );
        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .build();
        Integer totalPaymentAmount = 3000;

        // when
        Order order = Order.createOrder(userId, orderItems, coupon, totalPaymentAmount);

        // then
        assertThat(order)
                .satisfies(o -> {
                    assertThat(o.getUserId()).isEqualTo(userId);
                    assertThat(o.getTotalPaymentAmount()).isEqualTo(totalPaymentAmount);
                    assertThat(o.getTotalQuantity()).isEqualTo(3);  // 2 + 1
                    assertThat(o.getCouponId()).isEqualTo(1L);
                });
    }

    @Test
    @DisplayName("유효한 주문 생성 - 쿠폰 미적용")
    void createOrderWithoutCoupon() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .paymentAmount(1000)
                        .quantity(2)
                        .build()
        );
        Integer totalPaymentAmount = 2000;

        // when
        Order order = Order.createOrder(userId, orderItems, null, totalPaymentAmount);

        // then
        assertThat(order)
                .satisfies(o -> {
                    assertThat(o.getUserId()).isEqualTo(userId);
                    assertThat(o.getTotalPaymentAmount()).isEqualTo(totalPaymentAmount);
                    assertThat(o.getTotalQuantity()).isEqualTo(2);
                    assertThat(o.getCouponId()).isEqualTo(0L);
                });
    }

    @Test
    @DisplayName("주문 생성 시 유저 ID가 null이면 예외 발생")
    void throwExceptionWhenUserIdIsNull() {
        // given
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .paymentAmount(1000)
                        .quantity(1)
                        .build()
        );

        // when & then
        assertThatThrownBy(() -> Order.createOrder(null, orderItems, null, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID cannot be null");
    }

    @Test
    @DisplayName("주문 아이템이 null이면 예외 발생")
    void throwExceptionWhenOrderItemsNull() {
        // when & then
        assertThatThrownBy(() -> Order.createOrder(1L, null, null, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order items cannot be null");
    }
}
