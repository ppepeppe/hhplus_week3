package kr.hhplus.be.server.unit.coupon.service;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
import kr.hhplus.be.server.common.exception.CouponNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 ID로 쿠폰을 조회할 때 존재하지 않으면 예외 발생")
    void shouldThrowExceptionWhenCouponNotFound() {
        // given
        when(couponRepository.findCouponByCouponIdWithLock(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponService.getCouponWithLock(1L))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessageContaining("Coupon not found with ID: 1");
    }

    @Test
    @DisplayName("쿠폰 저장 시 null 객체 전달 시 예외 발생")
    void shouldThrowExceptionWhenSavingNullCoupon() {
        // when & then
        assertThatThrownBy(() -> couponService.saveCoupon(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coupon cannot be null");
    }
}
