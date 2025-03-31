package holt.picture.exception;

/**
 * Utility to help throwing exceptions in various conditions
 * @author Weiyang Wu
 * @date 2025/3/31 13:56
 */
public class ThrowUtils {
    /**
     * Throw an exception if the given condition is true
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * Throw an exception if the given condition is true with a given error code
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * Throw an exception with a given error code and a given message if the given condition is true
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
