package kr.hhplus.be.server.integration;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.payment.domain.models.Payment;
import kr.hhplus.be.server.apps.payment.domain.models.TransactionType;
import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CouponUseCaseTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository  userRepository;
    @Autowired
    private CouponUseCase couponUseCase;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;
    @BeforeEach
    void setUp() {

        // User 초기화
        User user = new User(null, "seongdo");
        userRepository.save(user);

        // Coupon 초기화
        Coupon coupon = new Coupon(null, "TESTCODE", 0.25, LocalDate.of(2025, 1, 11), 30, 0);
        couponRepository.save(coupon);

        // UserCoupon 초기화
        UserCoupon userCoupon = new UserCoupon(null, user.getUserId(), coupon.getCouponId(), false);
        userCouponRepository.save(userCoupon);
    }
    @Test
    @DisplayName("동시에 40명이 쿠폰을 신청 시 30명만 성공 (비관적 락)")
    void shouldAllowOnly30ParticipantsWhen40ApplySimultaneouslyWithPessimisticLock() throws InterruptedException {
        // given
        int numberOfThreads = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successfulRegistrations = new AtomicInteger(0);
        AtomicInteger failedRegistrations = new AtomicInteger(0);
        Coupon coupon = couponRepository.findCouponByCouponId(1L);
        for (int i = 2; i <= numberOfThreads + 1; i++) {
            final long userId = i;
            executorService.submit(() -> {
                try {
                    couponUseCase.issueCoupon(userId, coupon.getCouponId());
                    successfulRegistrations.incrementAndGet();
                    System.out.println("User " + userId + " 쿠폰 발급 완료.");
                } catch (Exception e) {
                    failedRegistrations.incrementAndGet();
                    System.out.println("User " + userId + " 쿠폰 발급 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Coupon updatedCoupon = couponRepository.findCouponByCouponId(coupon.getCouponId());
        assertEquals(30, updatedCoupon.getCurrentCount()); // 성공적으로 발급된 쿠폰 수
        assertEquals(30, successfulRegistrations.get()); // 성공한 신청
        assertEquals(10, failedRegistrations.get()); // 실패한 신청
    }
    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 삭제
        jdbcTemplate.execute("TRUNCATE TABLE coupon");
        jdbcTemplate.execute("TRUNCATE TABLE user_coupon");
        jdbcTemplate.execute("TRUNCATE TABLE user");

    }
}
