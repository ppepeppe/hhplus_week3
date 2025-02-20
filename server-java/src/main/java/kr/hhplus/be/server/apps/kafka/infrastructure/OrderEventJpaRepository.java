package kr.hhplus.be.server.apps.kafka.infrastructure;

import kr.hhplus.be.server.apps.kafka.domain.models.OrderEvent;
import kr.hhplus.be.server.apps.kafka.domain.models.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderEventJpaRepository extends JpaRepository<OrderEvent, Long> {
    List<OrderEvent> findByStatus(OutboxStatus status);
    List<OrderEvent> findByStatusAndFailCountLessThan(OutboxStatus status, int failCount);
}
