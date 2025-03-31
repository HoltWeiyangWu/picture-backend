package holt.picture.exception;

import lombok.Getter;

/**
 * Enumerate all the potential errors
 * @author Weiyang Wu
 * @date 2025/3/31 13:06
 */
@Getter
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(400000, "params error"),
    NOT_LOGIN_ERROR(40100, "not login"),
    NO_AUTH_ERROR(40101, "no authorisation"),
    NOT_FOUND_ERROR(40400, "not found"),
    FORBIDDEN_ERROR(40300, "forbidden"),
    SYSTEM_ERROR(50000, "system error"),
    OPERATION_ERROR(50001, "operation error");


    /**
     * Error code
     */
    private final int code;

    /**
     * Message along with the error code
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
