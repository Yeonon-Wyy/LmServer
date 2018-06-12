package top.yeonon.lmserver.http;

/**
 * 将Request和Response封装到一起
 *
 * @Author yeonon
 * @date 2018/6/10 0010 14:25
 **/
public class LmWebRequest {

    private final LmRequest lmRequest;
    private final LmResponse lmResponse;

    public LmWebRequest(LmRequest lmRequest, LmResponse lmResponse) {
        this.lmRequest = lmRequest;
        this.lmResponse = lmResponse;
    }

    public LmRequest getLmRequest() {
        return lmRequest;
    }

    public LmResponse getLmResponse() {
        return lmResponse;
    }
}
