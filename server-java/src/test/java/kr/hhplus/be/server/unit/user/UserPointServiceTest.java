package kr.hhplus.be.server.unit.user;

import kr.hhplus.be.server.apps.user.domain.models.entity.UserPoint;
import kr.hhplus.be.server.apps.user.domain.repository.UserPointRepository;
import kr.hhplus.be.server.apps.user.domain.service.UserPointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kr.hhplus.be.server.apps.user.utils.UserPointValidator.*;
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

    /**
     * 사용자 포인트 조회의 성공 케이스를 테스트합니다.
     */
    @Test
    @DisplayName("사용자 ID로 해당 사용자의 포인트를 조회한다.")
    void shouldRetrieveUserPointByUserIdSuccessfully() {
        // given
        int point = 1000;
        when(userPointRepository.findUserPointByUserId(USER_ID))
                .thenReturn(new UserPoint(USER_ID, point));

        // when
        Integer userPoint = userPointService.getUserPointByUserId(USER_ID);

        // then
        assertThat(userPoint).isEqualTo(point);
    }

    /*
    *  TODO 사용자 ID로 유저가 없는 경우
    * */

    /**
     * 사용자 포인트 충전의 성공 케이스를 테스트합니다.
     */
    @Test
    @DisplayName("사용자 Id로 해당 사용자의 포인트를 충전한다.")
    void shouldChargeUserPointByUserIdSuccessfully() {
        // given
        Integer originalPoint = 10000;
        Integer chargePoint = 20000;

        int point = 1000;
        when(userPointRepository.findUserPointByUserId(USER_ID))
                .thenReturn(new UserPoint(USER_ID, originalPoint));

        when(userPointRepository.save(new UserPoint(USER_ID, originalPoint + chargePoint)))
                .thenReturn(new UserPoint(USER_ID, originalPoint + chargePoint));

        // when
        UserPoint chargeUser = userPointService.chargeUserPoint(USER_ID, chargePoint);

        assertThat(chargeUser.getPoint()).isEqualTo(originalPoint + chargePoint);
    }

    /**
     * 포인트 충전 시 한 번에 충전할 수 있는 최대 포인트를 검증하는 테스트입니다.
     */
    @Test
    @DisplayName("포인트의 잔고는 최대 " + 100000 + "까지 충전이 가능합니다")
    void shouldThrowExceptionWhenChargingPointsExceedingMaxLimit() {
        // given
        Integer chargePoint = 100001;
        // when // then
        assertThatThrownBy(() -> userPointService.chargeUserPoint(USER_ID, chargePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트는 한 번에 최대 %d까지 충전할 수 있습니다.", MAX_AMOUNT);
    }

    /**
     * 포인트 충전 시 잔고에 대한 최대 충전 금액에 대한 테스트입니다.
     */
    @Test
    @DisplayName("포인트의 최대 잔고는 " + 1000000 + " 까지 충전이 가능합니다")
    void shouldThrowExceptionWhenChargingPointsExceedingTotalLimit() {
        // given
        Integer originPoint = 999000;
        Integer chargePoint = 2000;

        when(userPointRepository.findUserPointByUserId(USER_ID))
                .thenReturn(new UserPoint(USER_ID, originPoint));

        // when
        assertThatThrownBy(() ->
                userPointService.chargeUserPoint(USER_ID, chargePoint))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("포인트는 최대 %d까지 충전이 가능합니다.", MAX_TOTAL_POINTS);
    }
    /**
     *  포인트 충전 시 잔고에 대한 최소 충전 금액에 대한 테스트입니다.
     */
    @Test
    @DisplayName("포인트는 최소 " + 1000 + " 부터 충전할 수 있다.")
    void shouldThrowExceptionWhenChargingPointsBelowMinLimit() {
        // given
        Integer chargePoint = 990;
        assertThatThrownBy(() -> userPointService.chargeUserPoint(USER_ID, chargePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트는 최소 %d부터 충전할 수 있습니다.", MIN_AMOUNT);
    }

    /**
     * 상품 구매 시 포인트 차감에 대한 성공 케이스르 테스트합니다.
     */
    @Test
    @DisplayName("상품 구매시 UserPoint 의 point 가 차감 됩니다.")
    void shouldReduceUserPointsWhenOrderIsPlaced() {
        // given
        int point = 200000;
        int price = 100000;
        when(userPointRepository.findUserPointByUserIdWithLock(USER_ID))
                .thenReturn(new UserPoint(USER_ID, point));
        when(userPointRepository.save(new UserPoint(USER_ID, point - price)))
                .thenReturn(new UserPoint(USER_ID, point - price));

        // when
        UserPoint userPoint = userPointService.orderUserPoint(USER_ID, price);

        // then
        assertThat(userPoint.getUserId()).isEqualTo(USER_ID);
        assertThat(userPoint.getPoint()).isEqualTo(point-price);

    }

    @Test
    @DisplayName("포인트 차감 실패: 차감하려는 포인트가 현재 포인트를 초과할 때 예외 발생")
    void shouldFailWhenDeductingMorePointsThanAvailable() {
        // Given
        long userId = 1L;
        UserPoint userPoint = new UserPoint(null, userId, 1000); // 현재 포인트는 1000
        when(userPointRepository.findUserPointByUserIdWithLock(USER_ID))
                .thenReturn(userPoint);;

        // When & Then
        Integer deductPoints = 2000;
        assertThatThrownBy(() -> userPointService.orderUserPoint(userId, deductPoints))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("포인트 부족: 사용 가능한 포인트는 %d, 차감하려는 포인트는 %d입니다.",
                        userPoint.getPoint(), 2000));

        // 포인트가 변하지 않았는지 확인
        UserPoint updatedUserPoint = userPointRepository.findUserPointByUserIdWithLock(userId);
        assertThat(updatedUserPoint.getPoint()).isEqualTo(1000);
    }
}
