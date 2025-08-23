package io.github.archipelagomw;

/**
 * Small container class for the outcome of client requests.
 * @param <T> The type of the data held
 */
public class APResult<T> {

    public static enum ResultCode {
        SUCCESS,
        DISCONNECTED,
        UNAUTHENTICATED,
        OTHER_ERROR;
    }

    private final ResultCode code;
    private final T value;

    private APResult(ResultCode code) {
        this(code, null);
    }

    private APResult(ResultCode code, T value) {
        this.code = code;
        this.value = value;
    }

    public static <T> APResult<T> success() {
        return new APResult<>(ResultCode.SUCCESS);
    }

    public static <T> APResult<T> success(T value) {
        return new APResult<>(ResultCode.SUCCESS, value);
    }

    public static <T> APResult<T> disconnected() {
        return new APResult<>(ResultCode.DISCONNECTED);
    }

    public static <T> APResult<T> unauthenticated() {
        return new APResult<>(ResultCode.UNAUTHENTICATED);
    }

    public static <T> APResult<T> error() {
        return new APResult<>(ResultCode.OTHER_ERROR);
    }

    public ResultCode getCode() {
        return code;
    }

    public T getValue() {
        return value;
    }
}
