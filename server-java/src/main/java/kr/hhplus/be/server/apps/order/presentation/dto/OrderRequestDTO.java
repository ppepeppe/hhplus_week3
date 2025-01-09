package kr.hhplus.be.server.apps.order.presentation.dto;

import kr.hhplus.be.server.apps.order.domain.models.dto.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderRequestDTO {
    public List<OrderItemDTO> orderItems;
    public Long couponId;

    // 기본 생성자
    public OrderRequestDTO() {
        this.couponId = 0L; // 기본값 설정
    }
}
