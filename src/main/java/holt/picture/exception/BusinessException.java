package holt.picture.exception;

import lombok.Getter;

/**
 * Exception with error code and message
 * @author Weiyang Wu
 * @date 2025/3/31 13:12
 */

@Getter
public class BusinessException extends RuntimeException{
    private final int code;

    /**
     * Exception with customised error code and message
     */
    public BusinessException(final int code, final String message) {
        super(message);
        this.code = code;
    }

    /**
     * Exception with defined error code and message
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * Exception with defined error code and customised message
     */
    public BusinessException(ErrorCode errorCode, final String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
