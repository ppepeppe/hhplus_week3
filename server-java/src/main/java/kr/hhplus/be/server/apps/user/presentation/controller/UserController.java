package kr.hhplus.be.server.apps.user.presentation.controller;

import kr.hhplus.be.server.apps.user.presentation.dto.ChargeBalanceRequest;
import kr.hhplus.be.server.apps.user.presentation.dto.GetPointRequest;
import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    // 잔액 조회
    @GetMapping("/pont")
    public Integer getPointByUserId(GetPointRequest getPointRequest) {

        return 10000;
    }

    // 잔액 충전
    @PostMapping("/{userId}/charge")
    public Integer chargePoint(@PathVariable Long userId, @RequestBody ChargeBalanceRequest chargeBalanceRequest) {
        return 10000 + chargeBalanceRequest.getPoint();
    }
}
