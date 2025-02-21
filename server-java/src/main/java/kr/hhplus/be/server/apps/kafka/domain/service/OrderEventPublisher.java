package kr.hhplus.be.server.apps.kafka.domain.service;

import kr.hhplus.be.server.apps.kafka.domain.models.OrderEvent;
import kr.hhplus.be.server.apps.kafka.domain.models.OutboxStatus;
import kr.hhplus.be.server.apps.kafka.domain.repository.OrderEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OrderEventPublisher {

    private final OrderEventRepository orderEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    public OrderEventPublisher(OrderEventRepository orderEventRepository,
                               KafkaTemplate<String, String> kafkaTemplate) {
        this.orderEventRepository = orderEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // 예시로 1분마다 실행
    @Scheduled(fixedDelay = 60000)
    public void publishPendingEvents() {
        List<OrderEvent> pendingEvents = orderEventRepository.findByStatusAndFailCountLessThan(OutboxStatus.READY, 5);
        for (OrderEvent event : pendingEvents) {
            try {
                System.out.println("11");
                // Kafka로 이벤트 발행 (토픽 등은 상황에 맞게 설정)
                kafkaTemplate.send("order-events", event.getPayload()).get(10, TimeUnit.SECONDS);
                // 전송 성공 시 상태 업데이트
                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(LocalDateTime.now());
                orderEventRepository.save(event);
            } catch (Exception e) {
                System.out.println("12");
                event.setStatus(OutboxStatus.FAILED);
                event.setFailCount(event.getFailCount() + 1);
                event.setErrorMessage(e.getMessage());
                orderEventRepository.save(event);
                // 발행 실패 시 로그 기록 (필요하면 재시도 로직 또는 FAILED 상태 업데이트 추가)
                log.error("Failed to publish order event with id {}: {}", event.getId(), e.getMessage());
            }
        }
    }
}
