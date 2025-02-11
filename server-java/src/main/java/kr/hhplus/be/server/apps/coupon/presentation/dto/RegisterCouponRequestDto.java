package kr.hhplus.be.server.apps.coupon.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterCouponRequestDto {
    private Long couponId;
    private String code;
    private Double discountPercent;
    private LocalDate validDate;
    private Integer maxCount;
    private Integer currentCount;
}
