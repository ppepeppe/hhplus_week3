package kr.hhplus.be.server.apps.order.domain.repository;

import kr.hhplus.be.server.apps.order.domain.models.entity.Order;

public interface OrderRepository {
    Order save(Order order);
}
