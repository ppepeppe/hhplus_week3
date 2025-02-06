package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.order.application.facade.OrderFacade;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.payment.domain.models.Payment;
import kr.hhplus.be.server.apps.payment.domain.models.TransactionType;
import kr.hhplus.be.server.apps.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.repository.UserPointRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderFacadeConcurrencyIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPointRepository userPointRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @BeforeEach
    void setUp() {
        // Product 초기화
        Product productA = new Product(null, "Product A", 1000, 10, 0);
        productRepository.save(productA);
        // User 및 UserPoint 초기화
        List<User> users = Arrays.asList(
                new User(null, "seongdo"),
                new User(null, "john"),
                new User(null, "jane"),
                new User(null, "alice"),
                new User(null, "bob"),
                new User(null, "charlie"),
                new User(null, "david"),
                new User(null, "eve"),
                new User(null, "frank"),
                new User(null, "grace"),
                new User(null, "hannah"),
                new User(null, "ivan")
        );
        // Coupon 초기화
        Coupon coupon = Coupon.builder()
                .code("TESTCODE")
                .discountPercent(0.25)
                .validDate(LocalDate.of(2025, 1, 11))
                .maxCount(30)
                .currentCount(0)
                .build();
        couponRepository.save(coupon);
        List<UserPoint> userPoints = new ArrayList<>();

        for (User user : users) {
            userRepository.save(user);

            // 각 User에 대한 초기 포인트 설정
            int initialPoint = 100000 + (int) (Math.random() * 100000); // 10만 ~ 20만 포인트 랜덤 설정
            userPoints.add(new UserPoint(null, user.getUserId(), initialPoint));
            Payment payment = new Payment(null, user.getUserId(), initialPoint, TransactionType.CHARGE);
            paymentRepository.save(payment);
            // UserCoupon 초기화
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(user.getUserId())
                    .couponId(coupon.getCouponId())
                    .isUsed(false)
                    .build();

            userCouponRepository.save(userCoupon);

            userPointRepository.flush();


        }

        // UserPoint 저장
        for (UserPoint userPoint : userPoints) {
            userPointRepository.save(userPoint);
        }

    }

    @Test
    @DisplayName("동시에 12명의 사용자가 상품 개수 10개인 상품을 동시에 주문(비관적락)")
    public void testConcurrentOrders() throws InterruptedException {
        int numberOfUsers = 12; // 10명의 사용자
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        for (int i = 1; i <= numberOfUsers; i++) {
            final long userId = i;
            executorService.submit(() -> {
                try {

                    OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                            .productId(1L)
                            .paymentAmount(1000)
                            .quantity(1)
                            .build();
                    System.out.println("호출전" + userId);
                    orderFacade.placeOrder(userId, 0L, List.of(orderItemDTO));
                    System.out.println("User " + userId + " 주문 성공.");
                } catch (Exception e) {
                    System.out.println("User " + userId + " 주문 실패: " + e.getMessage());

                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // 최종 검증: 남은 재고가 0이어야 하고, 1명의 주문은 실패해야 함
        Optional<Product> product = productRepository.findProductByProductId(1L);
        assertEquals(0, product.get().getQuantity());
    }
    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 삭제
        jdbcTemplate.execute("TRUNCATE TABLE coupon");
        jdbcTemplate.execute("TRUNCATE TABLE user_coupon");
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("TRUNCATE TABLE user");
        jdbcTemplate.execute("TRUNCATE TABLE user_point");
        jdbcTemplate.execute("TRUNCATE TABLE payment");
        jdbcTemplate.execute("TRUNCATE TABLE user");
    }
}
