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
     * 在业务逻辑之前处理（即controller之前）
     * @param request 请求
     * @param response 响应
     * @return 若通过则返回True，否则返回False
     */
    boolean preHandler(LmRequest request, LmResponse response);

    /**
     * 在业务逻辑之后处理（即controller之后）
     * @param request 请求
     * @param response 响应
     *                 无返回值
     */
    void postHandler(LmRequest request, LmResponse response);
}
