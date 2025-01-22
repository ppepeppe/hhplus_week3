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
    @Version
    private int version;

}
