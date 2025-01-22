package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.order.application.facade.OrderFacade;
import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import kr.hhplus.be.server.apps.order.domain.models.entity.Order;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderFacadeIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPointRepository userPointRepository;
    @Autowired
    private ProductRepository productRepository;
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
        // Coupon 초기화
        Coupon coupon = Coupon.builder()
                .code("TESTCODE")
                .discountPercent(0.25)
                .validDate(LocalDate.of(2025, 1, 11))
                .maxCount(30)
                .currentCount(0)
                .build();
        couponRepository.save(coupon);

        // UserCoupon 초기화
        UserCoupon userCoupon = new UserCoupon(null, user.getUserId(), coupon.getCouponId(), false);
        userCouponRepository.save(userCoupon);
    }

    @Test
    @DisplayName("주문요청에 대한 성공 테스트")
    public void testOrderPlaceWithCoupon() {
        // given 주문 데이터 준비
        Order order = Order.builder()
                .userId(1L)
                .totalPaymentAmount(1000)
                .totalQuantity(1)
                .build();
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .productId(1L)
                .paymentAmount(1000)
                .quantity(1)
                .build();
        List<OrderItemDTO> orderItems = List.of(orderItemDTO);

        // When: OrderFacade를 호출
        Order order1 = orderFacade.placeOrder(1L, 1L, orderItems);

        // Then: 결과 검증
        assertNotNull(order1);
        assertEquals(750, order1.getTotalPaymentAmount()); // 할인 금액 검증
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
