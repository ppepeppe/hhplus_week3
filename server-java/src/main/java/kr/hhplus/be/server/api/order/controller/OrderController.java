package kr.hhplus.be.server.api.order.controller;

import kr.hhplus.be.server.api.order.dto.OrderRequestDTO;
import kr.hhplus.be.server.api.order.dto.OrderResponseDTO;
import kr.hhplus.be.server.domain.order.models.OrderItems;
import kr.hhplus.be.server.domain.order.models.Orders;
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

        return new OrderResponseDTO(new Orders(1L, 1L, 2000, 2),
                List.of(new OrderItems(1L, 1L, 1L, 1000,  1),
                        new OrderItems(2L, 1L, 2L, 1000, 1)));
    }
}
