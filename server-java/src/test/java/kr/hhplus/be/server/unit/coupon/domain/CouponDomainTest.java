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

        double totalAmount = 10000;

        // when
        double discount = coupon.calculateDiscount(totalAmount, coupon.getDiscountPercent());

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
        assertThatThrownBy(() -> coupon.calculateDiscount(-1, coupon.getDiscountPercent()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total amount must be greater than 0");
    }
}
