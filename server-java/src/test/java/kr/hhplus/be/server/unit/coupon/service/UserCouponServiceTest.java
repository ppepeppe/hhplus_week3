package kr.hhplus.be.server.unit.coupon.service;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import kr.hhplus.be.server.common.exception.UserCouponNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    @Test
    @DisplayName("유저 ID와 쿠폰 ID로 유저 쿠폰을 조회")
    void shouldRetrieveUserCouponByUserIdAndCouponId() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(1L)
                .userId(userId)
                .couponId(couponId)
                .isUsed(false)
                .build();

        when(userCouponRepository.findUserCouponByUserIdAndCouponId(userId, couponId)).thenReturn(Optional.of(userCoupon));

        // when
        Optional<UserCoupon> result = userCouponService.getUserCouponByUserIdAndCouponId(userId, couponId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userCoupon);
    }

    @Test
    @DisplayName("존재하지 않는 유저 쿠폰 조회 시 예외 발생")
    void shouldThrowExceptionWhenUserCouponNotFound() {
        // given
        Long userId = 1L;
        Long couponId = 999L;

        when(userCouponRepository.findUserCouponByUserIdAndCouponId(userId, couponId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userCouponService.getUserCouponByUserIdAndCouponId(userId, couponId))
                .isInstanceOf(UserCouponNotFoundException.class)
                .hasMessageContaining("UserCoupon not found for User ID: 1, Coupon ID: 999");
    }
}
