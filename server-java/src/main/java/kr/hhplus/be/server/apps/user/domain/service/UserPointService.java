package kr.hhplus.be.server.apps.user.domain.service;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.repository.UserPointRepository;
import kr.hhplus.be.server.apps.user.utils.UserPointValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPointService {
    private final UserPointRepository userPointRepository;
    /**
     *  유저id로 유저 조회
     */
    public Integer getUserPointByUserId(long userId) {
        UserPoint userPoint = userPointRepository.findUserPointByUserId(userId);
        return userPoint.getPoint();
    }
    /**
     * 유저 포인트 충전
     */
    public UserPoint chargeUserPoint(long userId, Integer points) {
        UserPoint userPoint = userPointRepository.findUserPointByUserId(userId);
        userPoint.addPoints(points);
        return userPointRepository.save(userPoint);
    }

    @Transactional
    public UserPoint orderUserPoint(long userId, Integer points) {
        UserPoint userPoint = userPointRepository.findUserPointByUserIdWithLock(userId);
        userPoint.deductPoints(points);
        return userPointRepository.save(userPoint);
    }
}
