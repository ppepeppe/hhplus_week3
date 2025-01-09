package kr.hhplus.be.server.apps.order.domain.repository;

import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);

}
