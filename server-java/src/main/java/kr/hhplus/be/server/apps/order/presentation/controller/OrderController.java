package kr.hhplus.be.server.apps.order.presentation.controller;

import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import kr.hhplus.be.server.apps.order.presentation.dto.OrderRequestDTO;
import kr.hhplus.be.server.apps.order.presentation.dto.OrderResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @PostMapping("/payment")
    public OrderResponseDTO order(@RequestBody OrderRequestDTO orderRequestDTO) {

        return new OrderResponseDTO(new Order(1L, 1L, 2000, 2),
                List.of(new OrderItem(1L, 1L, 1L, 1000,  1),
                        new OrderItem(2L, 1L, 2L, 1000, 1)));
    }
}
