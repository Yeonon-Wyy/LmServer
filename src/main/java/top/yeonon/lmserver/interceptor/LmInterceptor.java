package top.yeonon.lmserver.interceptor;

import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * LmInterceptor接口
 * @Author yeonon
 * @date 2018/5/25 0025 16:06
 **/
public interface LmInterceptor extends Comparable<LmInterceptor> {

    /**
     * 实现拦截逻辑的方法
     * @param request 请求
     * @param response 响应
     * @return 拦截通过返回True，否则返回False
     */
    boolean doInterceptor(LmRequest request, LmResponse response);
}
