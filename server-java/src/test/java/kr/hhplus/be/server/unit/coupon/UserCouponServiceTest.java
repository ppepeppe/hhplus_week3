package kr.hhplus.be.server.unit.coupon;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import kr.hhplus.be.server.common.exception.InvalidCouponException;
import kr.hhplus.be.server.common.exception.UserCouponNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {
    @Mock
    private UserCouponRepository userCouponRepository;
    @InjectMocks
    private UserCouponService userCouponService;

    /**
     * 유저ID로 쿠폰ID을 조회하는 성공케이스를 테스트 합니다.
     */
    @Test
    @DisplayName("유저ID로 쿠폰ID를 조회합니다.")
    void shouldRetrieveCouponIdByUserId() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = new UserCoupon(1L, userId, couponId, false);

        when(userCouponRepository.findUserCouponByUserIdAndCouponId(userId, couponId)).thenReturn(Optional.of(userCoupon));

        // when
        Optional<UserCoupon> userCoupon1 = userCouponService.getUserCouponByUserIdAndCouponId(userId, couponId);

        // then
        assertThat(userCoupon1.get()).isEqualTo(userCoupon);

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
    @Test
    @DisplayName("쿠폰 발급 중 입력값이 null일 경우 예외 발생")
    void shouldThrowExceptionWhenIssueCouponWithNullInputs() {
        // when & then
        assertThatThrownBy(() -> userCouponService.issueCoupon(null, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID and Coupon ID cannot be null.");

        assertThatThrownBy(() -> userCouponService.issueCoupon(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID and Coupon ID cannot be null.");
    }

    @Test
    @DisplayName("이미 사용된 쿠폰을 다시 사용하려는 경우 예외 발생")
    void shouldThrowExceptionWhenUsingAlreadyUsedCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(1L)
                .couponId(1L)
                .userId(1L)
                .isUsed(true)
                .build();

        // when & then
        assertThatThrownBy(() -> userCouponService.markCouponAsUsed(userCoupon))
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("The coupon has already been used.");
    }
}
