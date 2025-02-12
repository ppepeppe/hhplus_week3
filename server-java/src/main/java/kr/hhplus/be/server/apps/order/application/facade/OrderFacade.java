package kr.hhplus.be.server.apps.order.application.facade;

import kr.hhplus.be.server.apps.order.application.usecase.OrderUseCase;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderCommand;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderUseCase orderUseCase;

    public Order placeOrder(Long userId, List<OrderItemDTO> items, Long couponId) {
        return orderUseCase.placeOrder(
                OrderCommand.builder()
                        .userId(userId)
                        .orderItemDTOList(items)
                        .couponId(couponId)
                        .build()
        );
    }
}
