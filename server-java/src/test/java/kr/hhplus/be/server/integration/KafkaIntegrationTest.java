package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.config.kafka.OrderEventConsumer;
import kr.hhplus.be.server.config.kafka.OrderEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Tag("kafka")  // 태그로 구분
class KafkaIntegrationTest {
    @Autowired
    private OrderEventProducer producer;

    @Autowired
    private OrderEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer.clearReceivedOrders();  // 각 테스트 전에 초기화
    }

    @Test
    void whenSendMessage_thenShouldReceiveIt() throws Exception {
        // given
        Order order = Order.builder()
                .orderId(1L)
                .userId(1L)
                .totalPaymentAmount(1000)
                .build();

        // when
        producer.send(order);
        Thread.sleep(1000);  // Consumer가 메시지를 처리할 시간 대기

        // then
        List<Order> receivedOrders = consumer.getReceivedOrders();
        assertThat(receivedOrders.get(0))
                .extracting(Order::getOrderId, Order::getUserId, Order::getTotalPaymentAmount)
                .containsExactly(1L, 1L, 1000);
    }
}