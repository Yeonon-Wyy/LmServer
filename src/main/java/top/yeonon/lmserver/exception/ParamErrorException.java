package top.yeonon.lmserver.exception;

/**
 * @Author yeonon
 * @date 2018/5/23 0023 14:20
 **/
public class ParamErrorException extends RuntimeException {

    public ParamErrorException(String message) {
        super(message);
    }
}
