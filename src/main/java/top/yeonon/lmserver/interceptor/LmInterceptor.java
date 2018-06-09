package top.yeonon.lmserver.interceptor;

import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * @Author yeonon
 * @date 2018/5/25 0025 16:06
 **/
public interface LmInterceptor extends Comparable<LmInterceptor> {

    boolean doInterceptor(LmRequest request, LmResponse response);
}
