package kr.hhplus.be.server.apps.user.domain.service;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import kr.hhplus.be.server.apps.user.domain.repository.UserRepository;
import kr.hhplus.be.server.common.exception.UserNotFoundException;
import kr.hhplus.be.server.common.exception.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUserId(Long userId) {
        return userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "User not found with ID: " + userId
                ));
    }



}
