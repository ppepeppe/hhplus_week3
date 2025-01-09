package kr.hhplus.be.server.apps.user.domain.service;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import kr.hhplus.be.server.apps.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUserId(long userId) {

        return userRepository.findUserByUserId(userId);
    }


}
