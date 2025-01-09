package kr.hhplus.be.server.apps.order.infrastructure;

import kr.hhplus.be.server.apps.order.domain.models.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
    OrderItem save(OrderItem orderItem);
}
