package kr.hhplus.be.server.apps.user.domain.service;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPointService {
    private final UserPointRepository userPointRepository;

    /**
     * 유저포인트를 조회합니다.
     *
     * @param userId
     */
    public Integer getUserPointByUserId(long userId) {
        UserPoint userPoint = userPointRepository.findUserPointByUserId(userId);
        return userPoint.getPoint();
    }

    /**
     * 유저 포인트를 충전합니다.
     *
     * @param userId
     * @param points
     */
    public UserPoint chargeUserPoint(long userId, Integer points) {
        UserPoint userPoint = userPointRepository.findUserPointByUserId(userId);
        userPoint.addPoints(points);
        return userPointRepository.save(userPoint);
    }

    /**
     * 유저포인트를 사용합니다.
     *
     * @param userId
     * @param points
     */
    @Transactional
    public UserPoint deductUserPoint(long userId, Integer points) {
        UserPoint userPoint = userPointRepository.findUserPointByUserIdWithLock(userId);
        userPoint.deductPoints(points);
        return userPointRepository.save(userPoint);
    }
}
