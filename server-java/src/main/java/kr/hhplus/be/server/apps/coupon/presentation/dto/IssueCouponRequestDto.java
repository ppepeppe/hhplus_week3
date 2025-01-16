package kr.hhplus.be.server.apps.coupon.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueCouponRequestDto {
    public Long userId;
    public Long couponId;

}
