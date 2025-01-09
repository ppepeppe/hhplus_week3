package kr.hhplus.be.server.apps.user.infrastructure;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointJpaRepository extends JpaRepository<UserPoint, Long> {
    UserPoint findUserPointByUserId(Long userId);
}
