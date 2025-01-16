package kr.hhplus.be.server.apps.user.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargeBalanceRequest {
    public Long userId;
    public Integer point;
}
