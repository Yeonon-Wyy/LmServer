package top.yeonon.lmserver.filter;

import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 16:12
 **/
public interface LmFilter {

    void before(LmRequest request);

    void after(LmResponse response);
}
