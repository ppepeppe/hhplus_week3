package kr.hhplus.be.server.apps.kafka.infrastructure;

import kr.hhplus.be.server.apps.kafka.domain.models.OrderEvent;
import kr.hhplus.be.server.apps.kafka.domain.models.OutboxStatus;
import kr.hhplus.be.server.apps.kafka.domain.repository.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class OrderEventRepositoryImpl implements OrderEventRepository {
    private final OrderEventJpaRepository orderEventJpaRepository;
    @Override
    public List<OrderEvent> findByStatus(OutboxStatus status) {
        return orderEventJpaRepository.findByStatus(status);
    }

    @Override
    public void save(OrderEvent orderEvent) {
        orderEventJpaRepository.save(orderEvent);
    }

    @Override
    public void deleteAll() {
        orderEventJpaRepository.deleteAll();
    }

    @Override
    public Optional<OrderEvent> findById(Long id) {
        return orderEventJpaRepository.findById(id);
    }

    @Override
    public List<OrderEvent> findByStatusAndFailCountLessThan(OutboxStatus status, Integer failCount) {
        return orderEventJpaRepository.findByStatusAndFailCountLessThan(status, failCount);
    }
}