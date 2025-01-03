package kr.hhplus.be.server.api.coupon.controller;

import kr.hhplus.be.server.domain.coupon.models.Coupons;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@RestController
@RequestMapping("/coupon")
public class CouponController {
    private static final LocalDate DATE_TIME = LocalDate.of(2024, 12, 31);

    @PostMapping("/issue")
    public Coupons issueCoupon() {
        return new Coupons(1L, "code", 0.25, DATE_TIME);
    }
}
