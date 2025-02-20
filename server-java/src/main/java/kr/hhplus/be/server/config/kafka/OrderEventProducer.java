package kr.hhplus.be.server.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void send(Order order) {
        try {
            String message = new ObjectMapper().writeValueAsString(order);
            kafkaTemplate.send(TOPIC, message)
                    .thenAccept(result -> log.info("Message sent successfully: {}", message))
                    .exceptionally(throwable -> {
                        log.error("Failed to send message: {}", throwable.getMessage());
                        return null;
                    });
        } catch (JsonProcessingException e) {
            log.error("Error converting order to JSON", e);
        }
    }
}