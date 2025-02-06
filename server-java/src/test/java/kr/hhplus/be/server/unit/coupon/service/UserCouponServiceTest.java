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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
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

    @Test
    @DisplayName("유저 ID로 쿠폰 목록 조회 - 정상 케이스")
    void shouldReturnUserCouponsWhenUserHasCoupons() {
        // when
        Long userId = 1L;
        UserCoupon userCoupon1 = UserCoupon.builder()
                .userCouponId(1L)
                .userId(1L)
                .couponId(1L)
                .isUsed(false)
                .build();

        UserCoupon userCoupon2 = UserCoupon.builder()
                .userCouponId(2L)
                .userId(1L)
                .couponId(2L)
                .isUsed(false)
                .build();
        when(userCouponRepository.findAllByUserId(userId)).thenReturn(List.of(userCoupon1, userCoupon2));
        // then
        List<UserCoupon> userCoupons = userCouponService.getUserCouponListByUserId(userId);

        // given
        assertNotNull(userCoupons);
        assertThat(2).isEqualTo( userCoupons.size()); // 유저가 2개의 쿠폰을 가지고 있어야 함
    }

    @Test
    @DisplayName("validateIds - userId가 null일 경우 예외 발생")
    void shouldThrowExceptionWhenUserIdIsNull() {
        Long userId = null;
        Long couponId = 100L;

        assertThatThrownBy(() ->  userCouponService.validateIds(userId, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID and Coupon ID cannot be null.");

    }

    @Test
    @DisplayName("validateIds - couponId가 null일 경우 예외 발생")
    void shouldThrowExceptionWhenCouponIdIsNull() {
        Long userId = 1L;
        Long couponId = null;

        assertThatThrownBy(() ->  userCouponService.validateIds(userId, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID and Coupon ID cannot be null.");
    }

    @Test
    @DisplayName("validateIds - userId와 couponId가 모두 null일 경우 예외 발생")
    void shouldThrowExceptionWhenBothIdsAreNull() {
        Long userId = null;
        Long couponId = null;

        assertThatThrownBy(() ->  userCouponService.validateIds(userId, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID and Coupon ID cannot be null.");

    }

}
