package kr.hhplus.be.server.apps.kafka.domain.models;

import jakarta.persistence.*;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_outbox")
public class OrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType; // 예: "ORDER_COMPLETED"

    @Lob
    private String payload; // JSON 형태의 주문 이벤트 데이터

    @Enumerated(EnumType.STRING)
    private OutboxStatus status; // READY, SENT, FAILED 등
    private Integer failCount;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    // 생성 메서드 (빌더나 정적 팩토리 메서드 사용)
    public static OrderEvent createOrderCompletedEvent(Order order) {
        OrderEvent event = new OrderEvent();
        event.eventType = "ORDER_COMPLETED";
        // 주문 데이터를 JSON으로 직렬화 (예: ObjectMapper 활용)
        event.payload = convertOrderToJson(order);
        event.status = OutboxStatus.READY;
        event.createdAt = LocalDateTime.now();
        return event;
    }

    // JSON 변환 로직 (예시)
    private static String convertOrderToJson(Order order) {
        // 실제 구현에서는 ObjectMapper 등을 사용하여 직렬화
        return "{\"orderId\": " + order.getOrderId() + "}";
    }

}
