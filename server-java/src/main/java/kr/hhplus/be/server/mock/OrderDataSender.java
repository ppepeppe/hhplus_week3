package kr.hhplus.be.server.mock;

import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OrderDataSender {
    private final WebClient webClient;

    public OrderDataSender(WebClient.Builder webClientBuilder) {
        // baseUrl을 내부 API로 설정
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public void sendOrderData(Order order) {
        webClient.post()
                .uri("/mock/api") // mock API 호출
                .bodyValue(order)
                .retrieve()
                .bodyToMono(String.class) // String 응답 처리
                .doOnSuccess(response -> System.out.println("Response from Mock API: " + response))
                .doOnError(error -> System.err.println("Failed to call Mock API: " + error.getMessage()))
                .subscribe();
    }
}
