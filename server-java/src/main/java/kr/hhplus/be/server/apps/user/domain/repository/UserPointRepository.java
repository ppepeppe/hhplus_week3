package kr.hhplus.be.server.apps.user.domain.repository;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPointRepository {
    UserPoint findUserPointByUserId(Long userId);
    UserPoint save(UserPoint userPoint);
    void flush();
}
