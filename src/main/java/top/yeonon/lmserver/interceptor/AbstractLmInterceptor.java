package top.yeonon.lmserver.interceptor;

import top.yeonon.lmserver.annotation.Filter;
import top.yeonon.lmserver.annotation.Interceptor;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * @Author yeonon
 * @date 2018/6/9 0009 15:29
 **/
public class AbstractLmInterceptor implements LmInterceptor {

    @Override
    public boolean doInterceptor(LmRequest request, LmResponse response) {
        return false;
        //默认不做拦截
    }


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

