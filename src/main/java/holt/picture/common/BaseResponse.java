package holt.picture.common;

import holt.picture.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * Encapsulate response data with error code and data
 * @author Weiyang Wu
 * @date 2025/3/31 14:07
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * Error code
     */
    private int code;

    /**
     * Response data
     */
    private T data;

    /**
     * Message along with the status code
     */
    private String message;

    /**
     * Return a complete response
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * Return with no message
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * Return no data but error and message
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

}
