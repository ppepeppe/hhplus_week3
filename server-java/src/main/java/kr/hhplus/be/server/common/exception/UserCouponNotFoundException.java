package kr.hhplus.be.server.common.exception;

public class UserCouponNotFoundException extends RuntimeException {
    public UserCouponNotFoundException(String message) {
        super(message);
    }
}