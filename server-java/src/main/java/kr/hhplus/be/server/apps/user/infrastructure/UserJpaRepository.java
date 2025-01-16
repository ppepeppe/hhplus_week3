package kr.hhplus.be.server.apps.user.infrastructure;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUserId(Long Long);

}
