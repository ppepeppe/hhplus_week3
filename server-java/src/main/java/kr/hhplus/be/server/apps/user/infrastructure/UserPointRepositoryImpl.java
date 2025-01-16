package kr.hhplus.be.server.apps.user.infrastructure;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {
    private final UserPointJpaRepository userPointJpaRepository;
    @Override
    public UserPoint findUserPointByUserId(Long userId) {
        return userPointJpaRepository.findUserPointByUserId(userId);
    }

    @Override
    public UserPoint save(UserPoint userPoint) {

        return userPointJpaRepository.save(userPoint);
    }

    @Override
    public void flush() {
        userPointJpaRepository.flush();
    }
}
