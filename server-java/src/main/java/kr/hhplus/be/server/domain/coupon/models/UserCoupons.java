package kr.hhplus.be.server.domain.coupon.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserCoupons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    private Long userId;

    private Long couponId;

    private Boolean isUsed;
}