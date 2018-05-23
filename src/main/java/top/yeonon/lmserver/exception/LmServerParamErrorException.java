package top.yeonon.lmserver.exception;

/**
 * 参数错误异常
 * @Author yeonon
 * @date 2018/5/23 0023 14:20
 **/
public class LmServerParamErrorException extends RuntimeException {

    public LmServerParamErrorException(String message) {
        super(message);
    }
}
