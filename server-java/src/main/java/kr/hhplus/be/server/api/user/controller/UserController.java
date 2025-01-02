package kr.hhplus.be.server.api.user.controller;

import kr.hhplus.be.server.api.user.dto.ChargeBalanceRequest;
import kr.hhplus.be.server.domain.user.models.Users;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    // 잔액 조회
    @GetMapping("/{userId}")
    public Users getPointByUserId(@PathVariable Long userId) {

        return new Users(userId, "seongdo", 10000);
    }

    // 잔액 충전
    @PostMapping("/{userId}/charge")
    public Users chargePoint(@PathVariable Long userId, @RequestBody ChargeBalanceRequest chargeBalanceRequest) {
        return new Users(userId, "seongdo", 10000 + chargeBalanceRequest.getPoint());
    }
}
