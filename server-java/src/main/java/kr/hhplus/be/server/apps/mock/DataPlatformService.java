package kr.hhplus.be.server.apps.mock;

import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataPlatformService {
    private final RestTemplate restTemplate;
    private static final String MOCK_API_URL = "http://localhost:8090/api/data-platform/orders";

    @Async
    public void sendOrderData(Order order) {
        try {
            OrderDataRequest request = OrderDataRequest.builder()
                    .orderId(order.getOrderId())
                    .userId(order.getUserId())
                    .totalAmount(order.getTotalPaymentAmount())
                    .orderDate(LocalDateTime.now())
                    .build();

            ResponseEntity<Void> response = restTemplate.postForEntity(
                    MOCK_API_URL,
                    request,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully sent order data to platform - OrderId: {}", order.getOrderId());
            }
        } catch (Exception e) {
            log.error("Failed to send order data to platform - OrderId: {}", order.getOrderId(), e);
        }
    }
}