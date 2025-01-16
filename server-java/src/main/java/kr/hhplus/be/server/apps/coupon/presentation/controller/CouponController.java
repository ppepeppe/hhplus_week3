package kr.hhplus.be.server.apps.coupon.presentation.controller;

import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.presentation.dto.IssueCouponRequestDto;
import kr.hhplus.be.server.apps.coupon.presentation.dto.RegisterCouponRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {
    private final CouponUseCase couponUseCase;
    private static final LocalDate DATE_TIME = LocalDate.of(2024, 12, 31);
    @PostMapping("/reg")
    public ResponseEntity<Coupon> registerCoupon(@RequestBody RegisterCouponRequestDto registerCouponRequestDto) {
        return ResponseEntity.ok(couponUseCase.registerCoupon(
                registerCouponRequestDto.getCode(),
                registerCouponRequestDto.getDiscountPercent(),
                registerCouponRequestDto.getValidDate(),
                registerCouponRequestDto.getMaxCount(),
                registerCouponRequestDto.getCurrentCount()));
    }
    @PostMapping("/issue")
    public ResponseEntity<UserCoupon> issueCoupon(@RequestBody IssueCouponRequestDto issueCouponRequestDto) {

        return ResponseEntity.ok(couponUseCase.issueCoupon(issueCouponRequestDto.getUserId(), issueCouponRequestDto.getCouponId()));
    }
    @GetMapping("/{userId}/couponlist")
    public ResponseEntity<List<UserCoupon>> getUserCouponListByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(couponUseCase.getUserCouponListByUserId(userId));
    }

}
