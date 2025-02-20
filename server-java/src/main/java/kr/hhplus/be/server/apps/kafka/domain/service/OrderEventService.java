package kr.hhplus.be.server.apps.kafka.domain.service;

import kr.hhplus.be.server.apps.kafka.domain.models.OrderEvent;
import kr.hhplus.be.server.apps.kafka.domain.repository.OrderEventRepository;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderEventService {

    private final OrderEventRepository orderEventRepository;

    public OrderEventService(OrderEventRepository orderEventRepository) {
        this.orderEventRepository = orderEventRepository;
    }

    public void publishOrderCompletedEvent(Order order) {
        System.out.println(order);
        OrderEvent orderEvent = OrderEvent.createOrderCompletedEvent(order);
        orderEventRepository.save(orderEvent);
    }
}