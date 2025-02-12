package kr.hhplus.be.server.apps.order.domain.models.dto;

import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderPrepareResult {
    private final List<OrderItem> orderItems;
    private final Integer totalAmount;
}