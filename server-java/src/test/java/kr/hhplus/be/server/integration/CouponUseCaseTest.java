package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.coupon.application.facade.CouponFacade;
import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.apps.user.domain.models.entity.User;
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

import java.time.LocalDate;
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
    private CouponService couponService;
    @Autowired
    private CouponUseCase couponUseCase;
    @Autowired
    private CouponFacade couponFacade;

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
        couponService.registerCoupon(coupon.getCouponId(), 30);
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
        Coupon coupon = couponService.getCouponById(1L);
        for (int i = 2; i <= numberOfThreads + 1; i++) {
            final long userId = i;
            executorService.submit(() -> {
                try {
                    UserCoupon userCoupon = couponFacade.issueCoupon(userId, coupon.getCode(), coupon.getCouponId());
                    successfulRegistrations.incrementAndGet();
                } catch (Exception e) {
                    failedRegistrations.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        // Redis에서 최종 쿠폰 개수 확인x
        Integer remainingStock = couponService.getCouponStock(1L);

        assertEquals(0, remainingStock); // 30개 발급 완료 후 남은 재고는 0
        assertEquals(30, successfulRegistrations.get()); // 30명 성공
        assertEquals(10, failedRegistrations.get()); // 10명 실패
    }
    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 삭제
        jdbcTemplate.execute("TRUNCATE TABLE coupon");
        jdbcTemplate.execute("TRUNCATE TABLE user_coupon");
        jdbcTemplate.execute("TRUNCATE TABLE user");

    }
}
