package kr.hhplus.be.server.apps.user.domain.service;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.repository.UserPointRepository;
import kr.hhplus.be.server.apps.user.utils.UserPointValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {
    private final UserPointRepository userPointRepository;
//    public Integer getUserPointByUserId(long userId) {
//        UserPoint userPoint = userPointRepository.findUserPointByUserId(userId);
//
//        return userPoint.getPoint();
//    }
//
//    public UserPoint chargeUserPoint(long userId, Integer point) {
//        UserPointValidator.validateChargeAmount(point);
//        UserPoint userPoint  = userPointRepository.findUserPointByUserId(userId);
//
//        UserPointValidator.validateTotalPoints(userPoint.getPoint(), point);
//        userPoint.setPoint(userPoint.getPoint() + point);
//
//        return userPointRepository.save(userPoint);
//    }
//    /**
//     * 주문시 포인트 차감
//     */
//    public UserPoint orderUserPoint(long userId, Integer point) {
//        UserPoint userPoint = userPointRepository.findUserPointByUserIdWithLock(userId);
//        // 포인트 부족 여부 확인
//        if (userPoint.getPoint() < point) {
//            throw new IllegalArgumentException(String.format("포인트 부족: 사용 가능한 포인트는 %d, 차감하려는 포인트는 %d입니다.",
//                    userPoint.getPoint(), point));
//        }
//        userPoint.setPoint(userPoint.getPoint() - point);
//
//        return userPointRepository.save(userPoint);
//    }
    public Integer getUserPointByUserId(long userId) {
        UserPoint userPoint = userPointRepository.findUserPointByUserId(userId);
        return userPoint.getPoint();
    }

    public UserPoint chargeUserPoint(long userId, Integer points) {
        UserPoint userPoint = userPointRepository.findUserPointByUserId(userId);
        userPoint.addPoints(points);
        return userPointRepository.save(userPoint);
    }

    public UserPoint orderUserPoint(long userId, Integer points) {
        UserPoint userPoint = userPointRepository.findUserPointByUserIdWithLock(userId);
        userPoint.deductPoints(points);
        return userPointRepository.save(userPoint);
    }
}
