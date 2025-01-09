package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.apps.payment.domain.models.Payment;
import kr.hhplus.be.server.apps.payment.domain.models.TransactionType;
import kr.hhplus.be.server.apps.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.apps.payment.domain.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    private static final long USER_ID = 1L;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private PaymentService paymentService;

    /**
     * 상품 주문시 결제 내역 성공 케이스를 테스트합니다.
     */
    @Test
    @DisplayName("상품 주문시 결제내역을 테스트합니다")
    void shouldReduceUserPointsWhenOrderIsPlaced() {
        // given

        when(paymentRepository.save(new Payment(1L, USER_ID, 10000, TransactionType.CHARGE)))
                .thenReturn(new Payment(1L, USER_ID, 10000, TransactionType.CHARGE));

        // when
        Payment payment = paymentService.updatePayment(new Payment(1L, USER_ID, 10000, TransactionType.CHARGE));
        // then
        assertThat(payment.getUserId()).isEqualTo(USER_ID);
        assertThat(payment.getPoint()).isEqualTo(10000);
        assertThat(payment.getType()).isEqualTo(TransactionType.CHARGE);
    }
}
