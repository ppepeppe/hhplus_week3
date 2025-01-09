package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.apps.user.domain.models.entity.User;
import kr.hhplus.be.server.apps.user.domain.repository.UserRepository;
import kr.hhplus.be.server.apps.user.domain.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
                .thenReturn(new User(USER_ID, "seongdo"));

        // when
        User user = userService.getUserByUserId(USER_ID);

        // then
        assertThat(user.getUserId()).isEqualTo(USER_ID);
        assertThat(user.getName()).isEqualTo("seongdo");

    }
}
