package kr.hhplus.be.server.unit.coupon.domain;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CouponDomainTest {

    @Test
    @DisplayName("할인을 정상적으로 계산한다.")
    void shouldCalculateDiscountSuccessfully() {
        // given
        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .code("쿠폰1")
                .discountPercent(0.25)
                .validDate(LocalDate.of(2025, 1, 11))
                .maxCount(30)
                .currentCount(0)
                .build();

        Integer totalAmount = 10000;

        // when
        double discount = coupon.calculateDiscount(totalAmount);

        // then
        assertThat(discount).isEqualTo(2500); // 10,000 * 0.25 = 2,500
    }

    @Test
    @DisplayName("잘못된 총 금액으로 할인 계산 시 예외 발생")
    void shouldThrowExceptionWhenTotalAmountIsInvalid() {
        // given
        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .code("쿠폰1")
                .discountPercent(0.25)
                .validDate(LocalDate.of(2025, 1, 11))
                .maxCount(30)
                .currentCount(0)
                .build();

        // when & then
        assertThatThrownBy(() -> coupon.calculateDiscount(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total amount must be greater than 0");
    }
    @Test
    @DisplayName("정상적인 입력값으로 Coupon 객체 생성 테스트")
    void shouldCreateCouponSuccessfully() {
        // given
        String code = "DISCOUNT2025";
        Double discountPercent = 15.0;
        LocalDate validDate = LocalDate.of(2025, 12, 31);
        Integer maxCount = 100;
        Integer currentCount = 0;

        // when
        Coupon coupon = Coupon.create(code, discountPercent, validDate, maxCount, currentCount);

        // then
        assertThat(coupon).isNotNull();  // 객체가 정상적으로 생성되었는지 확인
        assertThat(coupon.getCode()).isEqualTo(code);
        assertThat(coupon.getDiscountPercent()).isEqualTo(discountPercent);
        assertThat(coupon.getValidDate()).isEqualTo(validDate);
        assertThat(coupon.getMaxCount()).isEqualTo(maxCount);
        assertThat(coupon.getCurrentCount()).isEqualTo(currentCount);
    }

    @Test
    @DisplayName("code가 null이면 예외 발생")
    void shouldThrowExceptionWhenCodeIsNull() {
        assertThatThrownBy(() -> Coupon.create(null, 15.0, LocalDate.of(2025, 12, 31), 100, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coupon fields cannot be null.");
    }

    @Test
    @DisplayName("discountPercent가 null이면 예외 발생")
    void shouldThrowExceptionWhenDiscountPercentIsNull() {
        assertThatThrownBy(() -> Coupon.create("DISCOUNT2025", null, LocalDate.of(2025, 12, 31), 100, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coupon fields cannot be null.");
    }

    @Test
    @DisplayName("validDate가 null이면 예외 발생")
    void shouldThrowExceptionWhenValidDateIsNull() {
        assertThatThrownBy(() -> Coupon.create("DISCOUNT2025", 15.0, null, 100, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coupon fields cannot be null.");
    }

    @Test
    @DisplayName("maxCount가 null이면 예외 발생")
    void shouldThrowExceptionWhenMaxCountIsNull() {
        assertThatThrownBy(() -> Coupon.create("DISCOUNT2025", 15.0, LocalDate.of(2025, 12, 31), null, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coupon fields cannot be null.");
    }

    @Test
    @DisplayName("currentCount가 null이면 예외 발생")
    void shouldThrowExceptionWhenCurrentCountIsNull() {
        assertThatThrownBy(() -> Coupon.create("DISCOUNT2025", 15.0, LocalDate.of(2025, 12, 31), 100, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coupon fields cannot be null.");
    }
}
