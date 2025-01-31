package kr.hhplus.be.server.unit.user.domain;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserPointDomainTest {

    @Test
    @DisplayName("포인트 충전: 정상적인 포인트가 추가된다.")
    void shouldAddPointsSuccessfully() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000);

        // When
        userPoint.addPoints(500);

        // Then
        assertThat(userPoint.getPoint()).isEqualTo(1500);
    }

    @Test
    @DisplayName("포인트 차감: 정상적인 포인트가 차감된다.")
    void shouldDeductPointsSuccessfully() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000);

        // When
        userPoint.deductPoints(500);

        // Then
        assertThat(userPoint.getPoint()).isEqualTo(500);
    }

    @Test
    @DisplayName("포인트 충전 실패: 0보다 작은 값 추가 시 예외 발생")
    void shouldThrowExceptionWhenAddingNegativePoints() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000);

        // When & Then
        assertThatThrownBy(() -> userPoint.addPoints(-500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전 금액은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("포인트 차감 실패: 차감하려는 포인트가 현재 포인트를 초과할 때 예외 발생")
    void shouldThrowExceptionWhenDeductingMorePointsThanAvailable() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 1000);

        // When & Then
        assertThatThrownBy(() -> userPoint.deductPoints(2000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트 부족: 사용 가능한 포인트는 1000, 차감하려는 포인트는 2000입니다.");
    }
}
