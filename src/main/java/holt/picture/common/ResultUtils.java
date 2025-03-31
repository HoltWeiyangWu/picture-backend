package holt.picture.common;

import holt.picture.exception.ErrorCode;

/**
 * Utility to return encapsulated success/error response
 * @author Weiyang Wu
 * @date 2025/3/31 14:12
 */

public class ResultUtils {

    /**
     * Success response
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * Error response
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * Error response with customised code and message
     */
    public static BaseResponse<?> error(int code,  String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * Error response with defined code and customised message
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(),null, message);
    }
}
