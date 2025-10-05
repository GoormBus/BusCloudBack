package goorm.global.infra.exception.error;


public class GoormBusException extends RuntimeException {

    private final ErrorCode errorCode;

    public GoormBusException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GoormBusException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessage() + " → " + detailMessage);
        this.errorCode = errorCode;
    }


    public GoormBusException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public int getHttpStatusCode() {
        return errorCode.getHttpCode();
    }
}
