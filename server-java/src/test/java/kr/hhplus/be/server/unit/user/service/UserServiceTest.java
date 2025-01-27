package kr.hhplus.be.server.unit.user.service;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import kr.hhplus.be.server.apps.user.domain.repository.UserRepository;
import kr.hhplus.be.server.apps.user.domain.service.UserService;
import kr.hhplus.be.server.common.exception.UserNotFoundException;
import kr.hhplus.be.server.common.exception.vo.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {
    private static final long USER_ID = 1L;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;


    /**
     * 사용자 조회의 성공 케이스를 테스트 합니다.
     */
    @Test
    @DisplayName("사용자ID로 해당 사용자를 조회합니다.")
    void shouldRetrieveUserByUserIdSuccessfully() {
        // given
        when(userRepository.findUserByUserId(USER_ID))
                .thenReturn(Optional.of(new User(USER_ID, "seongdo")));

        // when
        User user = userService.getUserByUserId(USER_ID);

        // then
        assertThat(user.getUserId()).isEqualTo(USER_ID);
        assertThat(user.getName()).isEqualTo("seongdo");

    }
    /**
     * 등록되지 않은 사용자 조회에 대한  케이스를 테스트 합니다.
     */
    @Test
    @DisplayName("사용자ID로 등록되지 않은 사용자를 조회합니다.")
    void shouldThrowExceptionWhenUserNotFound() {
        Long invalidUserId = 999L; // 존재하지 않는 사용자 ID
        when(userRepository.findUserByUserId(invalidUserId))
                .thenReturn(Optional.empty()); // 사용자 조회 시 null 반환

        // when & then
        assertThatThrownBy(() -> userService.getUserByUserId(invalidUserId))
                .isInstanceOf(UserNotFoundException.class) // 예외 타입 확인
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND) // ErrorCode 검증
                .hasMessageContaining("User not found with ID: " + invalidUserId); // 예외 메시지 검증
    }
}
