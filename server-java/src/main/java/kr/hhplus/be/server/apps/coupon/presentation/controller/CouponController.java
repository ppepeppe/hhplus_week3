package kr.hhplus.be.server.apps.coupon.presentation.controller;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@RestController
@RequestMapping("/coupon")
public class CouponController {
    private static final LocalDate DATE_TIME = LocalDate.of(2024, 12, 31);

    @PostMapping("/issue")
    public Coupon issueCoupon() {
        return new Coupon(1L, "code", 0.25, DATE_TIME, 30, 1);
    }
}
