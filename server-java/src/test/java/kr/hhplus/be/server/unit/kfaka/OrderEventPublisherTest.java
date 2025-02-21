package kr.hhplus.be.server.unit.kfaka;

import kr.hhplus.be.server.apps.kafka.domain.models.OrderEvent;
import kr.hhplus.be.server.apps.kafka.domain.models.OutboxStatus;
import kr.hhplus.be.server.apps.kafka.domain.repository.OrderEventRepository;
import kr.hhplus.be.server.apps.kafka.domain.service.OrderEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@SpringBootTest
//@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderEventPublisherTest {

    @Mock
    private OrderEventRepository orderEventRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OrderEventPublisher orderEventPublisher;

    @Test
    @DisplayName("보류 중인 이벤트를 성공적으로 발행하는 경우")
    void publishPendingEvents_Success() {
        // Given
        OrderEvent event = createOrderEvent(OutboxStatus.READY);
        when(orderEventRepository.findByStatusAndFailCountLessThan(OutboxStatus.READY, 5))
            .thenReturn(List.of(event));
        
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        System.out.println(future.join());
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(future);

        // When
        orderEventPublisher.publishPendingEvents();

        // Then
        verify(orderEventRepository).save(argThat(savedEvent -> 
            savedEvent.getStatus() == OutboxStatus.SENT &&
            savedEvent.getSentAt() != null
        ));
    }

    @Test
    @DisplayName("이벤트 발행 실패 시 상태 업데이트 확인")
    void publishPendingEvents_Failure() {
        // Given
        OrderEvent event = createOrderEvent(OutboxStatus.READY);
        when(orderEventRepository.findByStatusAndFailCountLessThan(OutboxStatus.READY, 5))
                .thenReturn(List.of(event));

        // Kafka 발행 실패 시뮬레이션
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka send failed"));
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(future);

        // When
        orderEventPublisher.publishPendingEvents();

        // Then
        verify(orderEventRepository).save(argThat(savedEvent ->
                savedEvent.getStatus() == OutboxStatus.FAILED &&
                        savedEvent.getFailCount() == 1 &&
                        savedEvent.getErrorMessage() != null &&
                        savedEvent.getId().equals(event.getId())  // ID 비교 추가
        ));
    }
    @Test
    @DisplayName("최대 재시도 횟수를 초과한 이벤트는 처리되지 않음")
    void publishPendingEvents_MaxRetryExceeded() {
        // Given
        when(orderEventRepository.findByStatusAndFailCountLessThan(OutboxStatus.READY, 5))
            .thenReturn(Collections.emptyList());

        // When
        orderEventPublisher.publishPendingEvents();

        // Then
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    private OrderEvent createOrderEvent(OutboxStatus status) {
        return OrderEvent.builder()
            .id(1L)
            .payload("Test payload")
            .status(status)
            .failCount(0)
            .build();
    }
}