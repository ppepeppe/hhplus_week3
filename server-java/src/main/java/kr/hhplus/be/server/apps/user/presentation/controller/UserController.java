package kr.hhplus.be.server.apps.user.presentation.controller;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.service.UserPointService;
import kr.hhplus.be.server.apps.user.domain.service.UserService;
import kr.hhplus.be.server.apps.user.presentation.dto.ChargeBalanceRequest;
import kr.hhplus.be.server.apps.user.presentation.dto.GetPointRequest;
import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserPointService userPointService;
    // 잔액 조회
    @GetMapping("/point")
    public ResponseEntity<Integer> getPointByUserId(GetPointRequest getPointRequest) {

        return ResponseEntity.ok(userPointService.getUserPointByUserId(getPointRequest.getUserId()));
    }

    // 잔액 충전
    @PostMapping("/{userId}/charge")
    public ResponseEntity<UserPoint> chargePoint(@PathVariable Long userId, @RequestBody ChargeBalanceRequest chargeBalanceRequest) {
        return ResponseEntity.ok(userPointService.chargeUserPoint(userId, chargeBalanceRequest.getPoint()));
    }
}
