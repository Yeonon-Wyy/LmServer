package top.yeonon.lmserver.filter;

import top.yeonon.lmserver.http.LmRequest;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 16:12
 **/
public interface LmFilter extends Comparable<LmFilter> {

    /**
     * 在业务逻辑之前
     * @param request 请求
     */
    void doFilter(LmRequest request);
}
