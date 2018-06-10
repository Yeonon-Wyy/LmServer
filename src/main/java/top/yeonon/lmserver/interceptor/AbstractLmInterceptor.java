package top.yeonon.lmserver.interceptor;

import top.yeonon.lmserver.annotation.Interceptor;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * Interceptor的抽象类，建立抽象类的原因是方便做扩展，可以在抽象类上做很多通用的事情
 *
 * @Author yeonon
 * @date 2018/6/9 0009 15:29
 **/
public class AbstractLmInterceptor implements LmInterceptor {

    @Override
    public boolean preHandler(LmRequest request, LmResponse response) {
        return false;
    }

    @Override
    public void postHandler(LmRequest request, LmResponse response) {

    }

    /**
     * 排序的时候会默认使用这个方法
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(LmInterceptor o) {
        Interceptor interceptor1 = this.getClass().getAnnotation(Interceptor.class);
        Interceptor interceptor2 = o.getClass().getAnnotation(Interceptor.class);
        if (interceptor1 == null || interceptor2 == null) {
            return -1;
        }
        Integer order1 = interceptor1.order();
        Integer order2 = interceptor2.order();

        return order1.compareTo(order2);
    }
}

