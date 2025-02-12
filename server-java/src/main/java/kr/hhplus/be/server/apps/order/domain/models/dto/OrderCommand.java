package kr.hhplus.be.server.apps.order.domain.models.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderCommand {
    private Long userId;
    private List<OrderItemDTO> orderItemDTOList;
    private Long couponId;
}
