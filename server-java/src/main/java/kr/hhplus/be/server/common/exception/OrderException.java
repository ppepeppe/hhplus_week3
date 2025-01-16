package kr.hhplus.be.server.common.exception;

import kr.hhplus.be.server.common.exception.vo.ErrorCode;

public class OrderException extends RuntimeException {
    private final ErrorCode errorCode;

    public OrderException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
