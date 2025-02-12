package kr.hhplus.be.server.apps.coupon.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;
    private String code;
    private Double discountPercent;
    private LocalDate validDate;
    private Integer maxCount;
    private Integer currentCount;

    // 쿠폰 발급 가능 여부 확인 및 발급 수 증가
    public void incrementUsage() {
        if (currentCount >= maxCount) {
            throw new IllegalArgumentException("Coupon usage limit reached");
        }
        this.currentCount += 1;
    }

    // 할인 계산
    public Integer calculateDiscount(Integer totalAmount) {
        if (totalAmount <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than 0");
        }
        return (int) ((int) totalAmount * discountPercent);
    }

    public static Coupon create(String code, Double discountPercent, LocalDate validDate, Integer maxCount, Integer currentCount) {
        if (code == null || discountPercent == null || validDate == null || maxCount == null || currentCount == null) {
            throw new IllegalArgumentException("Coupon fields cannot be null.");
        }
        return Coupon.builder()
                .code(code)
                .discountPercent(discountPercent)
                .validDate(validDate)
                .maxCount(maxCount)
                .currentCount(currentCount)
                .build();
    }
}
