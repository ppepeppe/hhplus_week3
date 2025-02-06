package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.coupon.application.facade.CouponFacade;
import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.product.domain.models.Product;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CouponUseCaseConcurrencyFOFSTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository  userRepository;
    @Autowired
    private CouponFacade couponFacade;
    @Autowired
    private CouponService couponService;

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

        couponService.createCoupon(coupon.getCouponId(), 30, 0.25, 1);

    }
    @Test
    @DisplayName("BlockingQueue를 이용한 선착순 쿠폰 처리 테스트")
    public void testBlockingQueueInTest() throws InterruptedException {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger successfulRegistrations = new AtomicInteger(0);
        AtomicInteger failedRegistrations = new AtomicInteger(0);
        // 사용자 요청 enqueue
        for (long userId = 1; userId < 41 ; userId++) {
            final long id = userId;
            executorService.submit(() -> {
                queue.add(id); // 요청을 큐에 추가
                System.out.println("User " + id + " 요청 큐에 추가");
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        Optional<Coupon> coupon1 = couponRepository.findCouponByCouponId(1L);
        Coupon coupon = coupon1.get();
        System.out.println(queue);
        // 큐에서 요청을 순차적으로 처리하면서 orderFacade.placeOrder 호출
        for (int i = 0; i < 40; i++) {
            Long userId = queue.poll(); // 큐에서 선착순으로 꺼냄
            if (userId != null) {
                try {

                    couponFacade.issueCoupon(userId, coupon.getCouponId());
                    successfulRegistrations.incrementAndGet();
                    System.out.println("User " + userId + " 쿠폰 발급 완료");
                } catch (Exception e) {
                    failedRegistrations.incrementAndGet();
                    System.out.println("User " + userId + " 쿠폰 발급 실패: " + e.getMessage());
                }
            }
        }
    // then
        Coupon updatedCoupon = couponRepository.findCouponByCouponId(coupon.getCouponId()).get();
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
