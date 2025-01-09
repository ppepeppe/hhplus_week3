package kr.hhplus.be.server.apps.payment.domain.repository;

import kr.hhplus.be.server.apps.payment.domain.models.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
