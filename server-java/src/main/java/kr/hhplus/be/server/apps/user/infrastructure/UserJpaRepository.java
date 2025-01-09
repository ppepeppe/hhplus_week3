package kr.hhplus.be.server.apps.user.infrastructure;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    User findUserByUserId(long userId);
}
