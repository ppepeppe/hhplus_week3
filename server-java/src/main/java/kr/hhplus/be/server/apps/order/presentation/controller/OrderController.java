package kr.hhplus.be.server.apps.order.presentation.controller;

import kr.hhplus.be.server.apps.order.application.facade.OrderFacade;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.presentation.dto.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    public final OrderFacade orderFacade;
    /**
     * 상품 주문
     */
    @PostMapping("/payment")
    public ResponseEntity<Order> order(@RequestBody OrderRequestDTO orderRequestDTO) {
        Order order = orderFacade.placeOrder(orderRequestDTO.userId,orderRequestDTO.orderItems , orderRequestDTO.couponId);
        return ResponseEntity.ok(order);
    }
}
