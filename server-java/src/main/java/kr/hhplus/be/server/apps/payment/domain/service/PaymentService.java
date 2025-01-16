package kr.hhplus.be.server.apps.payment.domain.service;

import kr.hhplus.be.server.apps.payment.domain.models.Payment;
import kr.hhplus.be.server.apps.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    //
    public Payment updatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
