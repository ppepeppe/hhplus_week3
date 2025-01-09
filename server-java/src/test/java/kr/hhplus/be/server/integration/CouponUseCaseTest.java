package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
    private CouponUseCase couponUseCase;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("동시에 40명이 쿠폰을 신청 시 30명만 성공 (비관적 락)")
    void shouldAllowOnly30ParticipantsWhen40ApplySimultaneouslyWithPessimisticLock() throws InterruptedException {
        // given
//        Coupon coupon = new Coupon();
//        coupon.setCode("code1");
//        coupon.setDiscountPercent(0.25);
//        coupon.setMaxCount(30); // 최대 발급 가능 쿠폰 수
//        coupon.setCurrentCount(0); // 현재 발급된 쿠폰 수
//        coupon.setValidDate();
//        couponRepository.save(coupon);
        int numberOfThreads = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successfulRegistrations = new AtomicInteger(0);
        AtomicInteger failedRegistrations = new AtomicInteger(0);
        Coupon coupon = couponRepository.findCouponByCouponId(1L);
        for (int i = 1; i <= numberOfThreads; i++) {
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
}
