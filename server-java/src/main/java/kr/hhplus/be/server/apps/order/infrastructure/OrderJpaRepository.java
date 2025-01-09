package kr.hhplus.be.server.apps.order.infrastructure;

import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    Order save(Order order);
}
