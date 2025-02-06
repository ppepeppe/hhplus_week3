package kr.hhplus.be.server.apps.order.domain.models.entity;

import jakarta.persistence.*;
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
    // 주문 생성 메서드 (팩토리 메서드)
    public static Order createOrder(Long userId, List<OrderItemDTO> orderItems) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }

        int totalPaymentAmount = 0;
        for (OrderItemDTO orderItem : orderItems) {
            totalPaymentAmount += orderItem.getPaymentAmount();
        }
        int totalQuantity = 0;
        for (OrderItemDTO orderItem : orderItems) {
            totalQuantity += orderItem.getQuantity();
        }
        return Order.builder()
                .userId(userId)
                .totalPaymentAmount(totalPaymentAmount)
                .totalQuantity(totalQuantity)
                .build();
    }

    // 결제 금액 업데이트 메서드
    public void applyDiscount(double discountedAmount) {
        this.totalPaymentAmount = (int) discountedAmount;
    }
}
