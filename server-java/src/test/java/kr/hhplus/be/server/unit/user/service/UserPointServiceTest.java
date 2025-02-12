package kr.hhplus.be.server.unit.user.service;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.repository.UserPointRepository;
import kr.hhplus.be.server.apps.user.domain.service.UserPointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {
    private static final long USER_ID = 1L;

    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private UserPointService userPointService;

    @Test
    @DisplayName("사용자 ID로 포인트 조회 성공")
    void shouldRetrieveUserPointByUserIdSuccessfully() {
        // Given
        UserPoint expectedUserPoint = new UserPoint(USER_ID, 1000);
        when(userPointRepository.findUserPointByUserId(USER_ID))
                .thenReturn(expectedUserPoint);

        // When
        Integer userPoint = userPointService.getUserPointByUserId(USER_ID);

        // Then
        assertThat(userPoint).isEqualTo(1000);
        // repository의 findUserPointByUserId가 한 번 호출되었음을 검증
        verify(userPointRepository, times(1)).findUserPointByUserId(USER_ID);
        // 그 외의 불필요한 상호작용이 없음을 확인
        verifyNoMoreInteractions(userPointRepository);
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void shouldChargeUserPointSuccessfully() {
        // Given
        UserPoint initialUserPoint = new UserPoint(USER_ID, 1000);
        UserPoint updatedUserPoint = new UserPoint(USER_ID, 3000);
        when(userPointRepository.findUserPointByUserId(USER_ID)).thenReturn(initialUserPoint);
        when(userPointRepository.save(initialUserPoint)).thenReturn(updatedUserPoint);

        // When
        UserPoint chargedUserPoint = userPointService.chargeUserPoint(USER_ID, 2000);

        // Then
        assertThat(chargedUserPoint.getPoint()).isEqualTo(3000);
        // 올바른 메서드 호출이 있었는지 검증
        verify(userPointRepository, times(1)).findUserPointByUserId(USER_ID);
        verify(userPointRepository, times(1)).save(initialUserPoint);
        verifyNoMoreInteractions(userPointRepository);
    }

    @Test
    @DisplayName("포인트 충전 실패: 최대 한도를 초과할 경우")
    void shouldFailWhenChargingPointsExceedingTotalLimit() {
        // Given
        UserPoint highUserPoint = new UserPoint(USER_ID, 999000);
        when(userPointRepository.findUserPointByUserId(USER_ID)).thenReturn(highUserPoint);

        // When & Then
        assertThatThrownBy(() -> userPointService.chargeUserPoint(USER_ID, 2000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("총 포인트는 1,000,000을 초과할 수 없습니다.");
        verify(userPointRepository, times(1)).findUserPointByUserId(USER_ID);
        verifyNoMoreInteractions(userPointRepository);
    }

    @Test
    @DisplayName("포인트 차감 성공")
    void shouldDeductPointsSuccessfully() {
        // Given
        UserPoint initialUserPoint = new UserPoint(USER_ID, 1000);
        UserPoint updatedUserPoint = new UserPoint(USER_ID, 500);
        when(userPointRepository.findUserPointByUserIdWithLock(USER_ID)).thenReturn(initialUserPoint);
        when(userPointRepository.save(initialUserPoint)).thenReturn(updatedUserPoint);

        // When
        UserPoint result = userPointService.deductUserPoint(USER_ID, 500);

        // Then
        assertThat(result.getPoint()).isEqualTo(500);
        verify(userPointRepository, times(1)).findUserPointByUserIdWithLock(USER_ID);
        verify(userPointRepository, times(1)).save(initialUserPoint);
        verifyNoMoreInteractions(userPointRepository);
    }
}
