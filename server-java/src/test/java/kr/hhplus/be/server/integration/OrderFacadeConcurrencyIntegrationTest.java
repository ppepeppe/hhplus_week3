package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.order.application.facade.OrderFacade;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderDto;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class OrderFacadeConcurrencyIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private ProductRepository productRepository;
    @Test
    public void testConcurrentOrders() throws InterruptedException {
        int numberOfUsers = 11; // 11명의 사용자
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);

        for (int i = 0; i < numberOfUsers; i++) {
            executorService.submit(() -> {
                try {
                    OrderDto orderDto = OrderDto.builder()
                            .userId(1L)
                            .totalPaymentAmount(1000)
                            .totalQuantity(1)
                            .build();

                    OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                            .productId(1L)
                            .paymentAmount(1000)
                            .quantity(1)
                            .build();

                    orderFacade.placeOrder(1L, 0L, List.of(orderItemDTO));
                } catch (Exception e) {
                    System.out.println("주문 실패: " + e.getMessage());
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // 최종 검증: 남은 재고가 0이어야 하고, 1명의 주문은 실패해야 함
        Product product = productRepository.findProductByProductId(1L);
        assertEquals(0, product.getQuantity());
    }
}
