package kr.hhplus.be.server.apps.user.utils;

public class UserPointValidator {
    public static final Integer MAX_AMOUNT = 100000;
    public static final Integer MIN_AMOUNT = 1000;
    public static final Integer MAX_TOTAL_POINTS = 1000000;

    public static void validateChargeAmount(Integer amount) {
        if (amount > MAX_AMOUNT) {
            throw new IllegalArgumentException("포인트는 한 번에 최대 " + MAX_AMOUNT + "까지 충전할 수 있습니다.");
        }
        if (amount < MIN_AMOUNT) {
            throw new IllegalArgumentException("포인트는 최소 " + MIN_AMOUNT + "부터 충전할 수 있습니다.");
        }
    }

    public static void validateTotalPoints(Integer originalPoint, Integer amount) {
        if (originalPoint + amount > MAX_TOTAL_POINTS) {
            throw new IllegalStateException("포인트는 최대 " + MAX_TOTAL_POINTS + "까지 충전이 가능합니다.");
        }
    }

    public static void isNotEnoughPoints(Integer originalPoint, Integer usePoint) {
        if (originalPoint < usePoint) {
            throw new IllegalStateException("포인트가 부족하여 사용할 수 없습니다.");
        }
    }
}
