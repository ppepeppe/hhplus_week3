package kr.hhplus.be.server.api.order.dto;

import kr.hhplus.be.server.domain.order.models.OrderItems;
import kr.hhplus.be.server.domain.order.models.Orders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    public Orders orders;
    public List<OrderItems> orderItemsList;
}
