package top.yeonon.lmserver.interceptor;

import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * LmInterceptor接口
 * @Author yeonon
 * @date 2018/5/25 0025 16:06
 **/
public interface LmInterceptor extends Comparable<LmInterceptor> {

    boolean preHandler(LmRequest request, LmResponse response);

    void postHandler(LmRequest request, LmResponse response);
}
