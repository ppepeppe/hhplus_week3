package kr.hhplus.be.server.apps.user.infrastructure;

import kr.hhplus.be.server.apps.user.domain.repository.UserRepository;
import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJPARepository;


    @Override
    public User findUserByUserId(Long userId) {
        return userJPARepository.findUserByUserId(userId);
    }

    @Override
    public User save(User user) {
        return userJPARepository.save(user);
    }
}
