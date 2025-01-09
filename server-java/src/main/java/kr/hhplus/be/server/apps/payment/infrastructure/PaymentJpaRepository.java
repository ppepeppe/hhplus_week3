package kr.hhplus.be.server.apps.payment.infrastructure;

import kr.hhplus.be.server.apps.payment.domain.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
