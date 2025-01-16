package kr.hhplus.be.server.apps.user.domain.repository;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findUserByUserId(Long userId);
    User save(User user);
}
