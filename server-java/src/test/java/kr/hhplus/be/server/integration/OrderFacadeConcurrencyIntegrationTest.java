package kr.hhplus.be.server.integration;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.order.application.facade.OrderFacade;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderDto;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.apps.order.domain.repository.OrderRepository;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
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
        // User 초기화
        User user = new User(null, "seongdo");
        userRepository.save(user);

        // UserPoint 초기화
        UserPoint userPoint = new UserPoint(null, user.getUserId(), 200000);
        userPointRepository.save(userPoint);
        // Payment 초기화
        Payment payment = new Payment(null, user.getUserId(), 100000, TransactionType.CHARGE);
        paymentRepository.save(payment);

        // Coupon 초기화
        Coupon coupon = new Coupon(null, "TESTCODE", 0.25, LocalDate.of(2025, 1, 11), 30, 0);
        couponRepository.save(coupon);

        // UserCoupon 초기화
        UserCoupon userCoupon = new UserCoupon(null, user.getUserId(), coupon.getCouponId(), false);
        userCouponRepository.save(userCoupon);

        System.out.println("여기111긴해 ");
        System.out.println(userPointRepository.findUserPointByUserId(1L));
        userPointRepository.flush();
        System.out.println("여기222긴해 ");
        System.out.println(userPointRepository.findUserPointByUserId(1L));

    }

    @Test
    public void testConcurrentOrders() throws InterruptedException {
        int numberOfUsers = 11; // 11명의 사용자
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        System.out.println("여기긴해 ");
        System.out.println(userPointRepository.findUserPointByUserId(1L));
        for (int i = 0; i < numberOfUsers; i++) {
            executorService.submit(() -> {
                try {

                    OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                            .productId(1L)
                            .paymentAmount(1000)
                            .quantity(1)
                            .build();

                    orderFacade.placeOrder(1L, 0L, List.of(orderItemDTO));
                } catch (Exception e) {
                    System.out.println("주문 실패: " + e.getMessage());
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
