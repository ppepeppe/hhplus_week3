package kr.hhplus.be.server.apps.order.domain.models.dto;

import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinalAmountResult {
    private Integer finalAmount;
    private Coupon coupon;
}