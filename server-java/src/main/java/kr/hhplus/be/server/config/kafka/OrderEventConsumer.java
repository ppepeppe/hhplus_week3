package kr.hhplus.be.server.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final List<Order> receivedOrders = new ArrayList<>();  // 테스트를 위한 리스트

    @KafkaListener(topics = "order-events", groupId = "order-group")
    public void consume(String message) {
        try {
            Order order = new ObjectMapper().readValue(message, Order.class);
            log.info("Consumed message: {}", order);
            receivedOrders.add(order);  // 수신한 메시지 저장
        } catch (JsonProcessingException e) {
            log.error("Error parsing message: {}", e.getMessage());
        }
    }

    // 테스트를 위한 메서드들
    public List<Order> getReceivedOrders() {
        return new ArrayList<>(receivedOrders);
    }

    public void clearReceivedOrders() {
        receivedOrders.clear();
    }
}