package kr.hhplus.be.server.unit.coupon;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import kr.hhplus.be.server.common.exception.CouponNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {
    private static final long COUPON_ID = 1L;
    private static final long USER_ID = 1L;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;
    @InjectMocks
    private CouponService couponService;
    @InjectMocks
    private UserCouponService userCouponService;
    /**
     * 쿠폰 조회 성공케이스를 테스트합니다.
     */
    @Test
    @DisplayName("쿠폰ID로 해당 쿠폰을 조회한다")
    void shouldRetrieveCouponByCouponId() {
        // given
        when(couponRepository.findCouponByCouponIdWithLock(COUPON_ID))
                .thenReturn(new Coupon(COUPON_ID, "쿠폰1", 0.25, LocalDate.of(2025,1,1), 30, 0));
        // when
        Coupon coupon = couponService.getCouponWithLock(COUPON_ID);
        // then
        assertThat(coupon.getCouponId()).isEqualTo(COUPON_ID);
        assertThat(coupon.getCode()).isEqualTo("쿠폰1");
        assertThat(coupon.getDiscountPercent()).isEqualTo(0.25);
    }

    @Test
    @DisplayName("쿠폰 ID로 조회 시 쿠폰이 없을 때 예외 발생")
    void shouldThrowExceptionWhenCouponNotFoundWithLock() {
        // given
        when(couponRepository.findCouponByCouponIdWithLock(COUPON_ID)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> couponService.getCouponWithLock(COUPON_ID))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessageContaining("Coupon not found with ID: " + COUPON_ID);
    }
    /**
     * 쿠폰 발급 성공케이스를 테스트합니다
     */

    @Test
    @DisplayName("쿠폰ID, 사용자 ID로 해당 쿠폰을 해당 유저에게 발급한다")
    void shouldIssueCouponByCouponIdAndUserIdSuccessfully() {
        // given
//        when(couponRepository.findCouponByCouponIdWithLock(COUPON_ID))
//                .thenReturn(new Coupon(COUPON_ID, "쿠폰1", 0.25, LocalDate.of(2025, 1, 1), 30, 0));

//        when(couponRepository.save(any(Coupon.class)))
//                .thenReturn(new Coupon(COUPON_ID, "쿠폰1", 0.25, LocalDate.of(2025,1,1), 30, 1));

        when(userCouponRepository.save(any(UserCoupon.class)))
                .thenReturn(new UserCoupon(1L, 1L, 1L, true));

        // when
        UserCoupon userCoupon = userCouponService.issueCoupon(USER_ID, COUPON_ID);

        // then
        assertThat(userCoupon.getUserCouponId()).isEqualTo(1L);
        assertThat(userCoupon.getUserId()).isEqualTo(USER_ID);
        assertThat(userCoupon.getCouponId()).isEqualTo(COUPON_ID);
        assertThat(userCoupon.getIsUsed()).isEqualTo(true);
    }
    /**
     * 쿠폰으로 전체금액을 할인하는 성공케이스를 테스트합니다,
     */
    @Test
    @DisplayName("쿠폰으로 금액을 할인한다.")
    void shouldDiscountByCouponSuccessfully() {
        // given
        Long couponId = 1L;
        String code = "쿠폰1";
        Double discountPercent = 0.25;
        LocalDate validDate = LocalDate.of(2025,1,11);
        Integer totalAmount = 100000;
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .code(code)
                .discountPercent(discountPercent)
                .validDate(validDate)
                .maxCount(30)
                .currentCount(0)
                .build();

//        when(couponRepository.findCouponByCouponId(couponId)).thenReturn(coupon);

        // when
        Double discountAmount = couponService.calculateDiscount(coupon, totalAmount);

        // then
        assertThat(discountAmount).isEqualTo(25000);
        assertThat(totalAmount - discountAmount).isEqualTo(75000);
    }

    @Test
    @DisplayName("쿠폰 저장 시 null 객체 전달 시 예외 발생")
    void shouldThrowExceptionWhenSavingNullCoupon() {
        // when & then
        assertThatThrownBy(() -> couponService.saveCoupon(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coupon cannot be null");
    }
    @Test
    @DisplayName("쿠폰이 null일 때 할인 계산 예외 발생")
    void shouldThrowExceptionWhenCouponIsNullInCalculateDiscount() {
        // when & then
        assertThatThrownBy(() -> couponService.calculateDiscount(null, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coupon cannot be null");
    }

    @Test
    @DisplayName("금액이 0 이하일 때 할인 계산 예외 발생")
    void shouldThrowExceptionWhenTotalAmountIsInvalidInCalculateDiscount() {
        // given
        Coupon coupon = new Coupon(COUPON_ID, "쿠폰1", 0.25, LocalDate.of(2025, 1, 11), 30, 0);

        // when & then
        assertThatThrownBy(() -> couponService.calculateDiscount(coupon, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total amount must be greater than 0");
    }
    @Test
    @DisplayName("유저가 갖고 있는 쿠폰 정보를 조회한다.")
    void shouldRetrieveCouponByUserIdSuccessfully() {
        // given
        Coupon coupon = new Coupon(1L, "쿠폰1", 0.25, LocalDate.of(2025, 1, 11), 30, 0);
        Coupon coupon2 = new Coupon(2L, "쿠폰2", 0.25, LocalDate.of(2025, 1, 11), 30, 0);
        Coupon coupon3 = new Coupon(3L, "쿠폰3", 0.25, LocalDate.of(2025, 1, 11), 30, 0);

        UserCoupon userCoupon1 = new UserCoupon(1L, 1L, 1L, false);
        UserCoupon userCoupon2 = new UserCoupon(2L, 1L, 2L, false);
        UserCoupon userCoupon3 = new UserCoupon(3L, 1L, 3L, false);

        when(userCouponRepository.findAllByUserId(USER_ID)).thenReturn(List.of(userCoupon1, userCoupon2, userCoupon3));
        // when
        List<UserCoupon> userCouponList = userCouponService.getUserCouponListByUserId(USER_ID);

        // then
        assertThat(userCouponList.size()).isEqualTo(3);
        assertThat(userCouponList.get(0).getCouponId()).isEqualTo(1L);
        assertThat(userCouponList.get(1).getCouponId()).isEqualTo(2L);
        assertThat(userCouponList.get(2).getCouponId()).isEqualTo(3L);

    }
    @Test
    @DisplayName("신큐 쿠폰을 등록한다")
    void shouldIssueCouponSuccessfully() {
        // given
        Coupon newCoupon = new Coupon(2L, "쿠폰2", 0.25, LocalDate.of(2025, 1, 11), 30, 0);
        when(couponRepository.save(newCoupon)).thenReturn(newCoupon);

        // when
        Coupon coupon = couponService.saveCoupon(newCoupon);

        // then
        assertThat(coupon.getCouponId()).isEqualTo(2L);
    }

}
