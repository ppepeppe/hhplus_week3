package kr.hhplus.be.server.apps.user.domain.repository;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;

public interface UserRepository {
    User findUserByUserId(Long userId);
    User save(User user);
}
