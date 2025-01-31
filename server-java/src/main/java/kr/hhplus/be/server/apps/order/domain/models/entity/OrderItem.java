package kr.hhplus.be.server.apps.order.domain.models.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private Long orderId;

    private Long productId;

    private Integer paymentAmount;

    private Integer quantity;

    // OrderItem 생성 메서드
    public static OrderItem fromDTO(Order order, OrderItemDTO orderItemDTO) {
        if (orderItemDTO.getProductId() == null || orderItemDTO.getProductId() <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than 0");
        }
        if (orderItemDTO.getPaymentAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }
        if (orderItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        return OrderItem.builder()
                .orderId(order.getOrderId())
                .productId(orderItemDTO.getProductId())
                .paymentAmount(orderItemDTO.getPaymentAmount())
                .quantity(orderItemDTO.getQuantity())
                .build();
    }
}
