package kr.hhplus.be.server.apps.kafka.domain.repository;

import kr.hhplus.be.server.apps.kafka.domain.models.OrderEvent;
import kr.hhplus.be.server.apps.kafka.domain.models.OutboxStatus;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.apps.kafka.domain.models.OutboxStatus.READY;

public interface OrderEventRepository {
    List<OrderEvent> findByStatus(OutboxStatus status);
    void save(OrderEvent orderEvent);
    void deleteAll();
    Optional<OrderEvent> findById(Long id);
    List<OrderEvent> findByStatusAndFailCountLessThan(OutboxStatus status, Integer failCount);
}
