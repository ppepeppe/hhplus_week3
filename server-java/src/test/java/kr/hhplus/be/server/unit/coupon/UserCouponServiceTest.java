package kr.hhplus.be.server.unit.coupon;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.service.UserCouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        when(userCouponRepository.findUserCouponByUserIdAndCouponId(userId, couponId)).thenReturn(userCoupon);

        // when
        UserCoupon userCoupon1 = userCouponService.getUserCouponByUserIdAndCouponId(userId, couponId);

        // then
        assertThat(userCoupon1).isEqualTo(userCoupon);

    }
}
