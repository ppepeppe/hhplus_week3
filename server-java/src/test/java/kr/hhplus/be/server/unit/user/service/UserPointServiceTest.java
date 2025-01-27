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
import static org.mockito.Mockito.when;

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
        when(userPointRepository.findUserPointByUserId(USER_ID))
                .thenReturn(new UserPoint(USER_ID, 1000));

        // When
        Integer userPoint = userPointService.getUserPointByUserId(USER_ID);

        // Then
        assertThat(userPoint).isEqualTo(1000);
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void shouldChargeUserPointSuccessfully() {
        // Given
        UserPoint userPoint = new UserPoint(USER_ID, 1000);
        when(userPointRepository.findUserPointByUserId(USER_ID)).thenReturn(userPoint);
        when(userPointRepository.save(userPoint)).thenReturn(new UserPoint(USER_ID, 3000));

        // When
        UserPoint chargedUserPoint = userPointService.chargeUserPoint(USER_ID, 2000);

        // Then
        assertThat(chargedUserPoint.getPoint()).isEqualTo(3000);
    }

    @Test
    @DisplayName("포인트 충전 실패: 최대 한도를 초과할 경우")
    void shouldFailWhenChargingPointsExceedingTotalLimit() {
        // Given
        UserPoint userPoint = new UserPoint(USER_ID, 999000);
        when(userPointRepository.findUserPointByUserId(USER_ID)).thenReturn(userPoint);

        // When & Then
        assertThatThrownBy(() -> userPointService.chargeUserPoint(USER_ID, 2000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("총 포인트는 1,000,000을 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("포인트 차감 성공")
    void shouldDeductPointsSuccessfully() {
        // Given
        UserPoint userPoint = new UserPoint(USER_ID, 1000);
        when(userPointRepository.findUserPointByUserIdWithLock(USER_ID)).thenReturn(userPoint);
        when(userPointRepository.save(userPoint)).thenReturn(new UserPoint(USER_ID, 500));

        // When
        UserPoint updatedUserPoint = userPointService.orderUserPoint(USER_ID, 500);

        // Then
        assertThat(updatedUserPoint.getPoint()).isEqualTo(500);
    }
}
