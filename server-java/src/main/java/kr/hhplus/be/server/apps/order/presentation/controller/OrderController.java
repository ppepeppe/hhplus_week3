package kr.hhplus.be.server.apps.order.presentation.controller;

import kr.hhplus.be.server.apps.order.application.facade.OrderFacade;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.presentation.dto.OrderRequestDTO;
import kr.hhplus.be.server.apps.order.presentation.dto.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    public final OrderFacade orderFacade;
    @PostMapping("/payment")
    public OrderResponseDTO order(@RequestBody OrderRequestDTO orderRequestDTO) {
        orderFacade.placeOrder(orderRequestDTO.userId, orderRequestDTO.couponId, orderRequestDTO.orderItems);
        return new OrderResponseDTO();
    }
}
