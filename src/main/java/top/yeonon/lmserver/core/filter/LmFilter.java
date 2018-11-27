package top.yeonon.lmserver.core.filter;

import top.yeonon.lmserver.core.http.LmRequest;

/**
 * 过滤器接口，一般在使用的时候不会直接实现该方法，大多是继承@LmFilterAdapter适配器类
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
