package kr.hhplus.be.server.apps.coupon.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_coupon")
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    private Long userId;

    private Long couponId;

    private Boolean isUsed;
    public static UserCoupon create(Long userId, Long couponId) {
        return UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .isUsed(false)
                .build();
    }
    public void markAsUsed() {
        if (this.isUsed) {
            throw new IllegalArgumentException("The coupon has already been used.");
        }
        this.isUsed = true;
    }
}