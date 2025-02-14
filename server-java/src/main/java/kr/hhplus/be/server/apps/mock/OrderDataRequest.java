package kr.hhplus.be.server.apps.mock;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderDataRequest {
    private Long orderId;
    private Long userId;
    private Integer totalAmount;
    private LocalDateTime orderDate;
}