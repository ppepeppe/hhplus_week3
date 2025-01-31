package kr.hhplus.be.server.unit.coupon.domain;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserCouponDomainTest {

    @Test
    @DisplayName("이미 사용된 쿠폰을 다시 사용하려는 경우 예외 발생")
    void shouldThrowExceptionWhenUsingAlreadyUsedCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(1L)
                .userId(1L)
                .couponId(1L)
                .isUsed(true)
                .build();

        // when & then
        assertThatThrownBy(userCoupon::markAsUsed)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The coupon has already been used.");
    }
}
