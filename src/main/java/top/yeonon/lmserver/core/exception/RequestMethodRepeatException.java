package top.yeonon.lmserver.core.exception;

/**
 * @Author yeonon
 * @date 2018/11/28 0028 18:47
 **/
public class RequestMethodRepeatException extends RuntimeException {

    public RequestMethodRepeatException(String message) {
        super(message);
    }

    public RequestMethodRepeatException() {
    }
}
