package kr.hhplus.be.server.apps.user.infrastructure;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPointJpaRepository extends JpaRepository<UserPoint, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserPoint u WHERE u.userId = :userId")
    UserPoint findUserPointByUserIdWithLock(@Param("userId") long userId);
    UserPoint findUserPointByUserId(Long userId);
}
