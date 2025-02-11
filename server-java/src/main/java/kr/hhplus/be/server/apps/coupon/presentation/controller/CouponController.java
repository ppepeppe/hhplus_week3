package kr.hhplus.be.server.apps.coupon.presentation.controller;

import kr.hhplus.be.server.apps.coupon.application.facade.CouponFacade;
import kr.hhplus.be.server.apps.coupon.application.usecase.CouponUseCase;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.apps.coupon.domain.service.CouponService;
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
    private final CouponFacade couponFacade;
    private static final LocalDate DATE_TIME = LocalDate.of(2024, 12, 31);
    private final CouponService couponService;

    @PostMapping("/reg")
    public ResponseEntity<Coupon> registerCoupon(@RequestBody RegisterCouponRequestDto registerCouponRequestDto) {
        Coupon coupon = couponUseCase.registerCoupon(
                registerCouponRequestDto.getCode(),
                registerCouponRequestDto.getDiscountPercent(),
                registerCouponRequestDto.getValidDate(),
                registerCouponRequestDto.getMaxCount(),
                registerCouponRequestDto.getCurrentCount());
        couponService.registerCoupon(registerCouponRequestDto.getCouponId(), registerCouponRequestDto.getMaxCount());
        return ResponseEntity.ok(coupon);
    }

    @GetMapping("/{userId}/couponlist")
    public ResponseEntity<List<UserCoupon>> getUserCouponListByUserId(@PathVariable Long userId) {
        List<UserCoupon> userCoupons = couponUseCase.getUserCouponListByUserId(userId);
        return ResponseEntity.ok(userCoupons);
    }

    @GetMapping("users/{userId}/coupon/{couponId}")
    public ResponseEntity<UserCoupon> issueCoupon(@PathVariable Long userId, @PathVariable Long couponId) throws InterruptedException {
        UserCoupon userCoupon = couponFacade.issueCoupon(userId, "", couponId);
        return ResponseEntity.ok(userCoupon);
    }




}
