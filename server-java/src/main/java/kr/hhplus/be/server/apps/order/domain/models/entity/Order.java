package kr.hhplus.be.server.apps.order.domain.models.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    private Integer totalPaymentAmount;

    private Integer totalQuantity;
    private Long couponId;

    // 주문 생성 메서드 (팩토리 메서드)
    public static Order createOrder(Long userId, List<OrderItem> orderItems, Coupon coupon, Integer totalPaymentAmount) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        int totalQuantity = 0;
        for (OrderItem orderItem : orderItems) {
            totalQuantity += orderItem.getQuantity();
        }
        Long couponId = 0L;
        if (coupon != null) {
            couponId = coupon.getCouponId();
        }

        return Order.builder()
                .userId(userId)
                .totalPaymentAmount(totalPaymentAmount)
                .totalQuantity(totalQuantity)
                .couponId(couponId)
                .build();
    }
}
