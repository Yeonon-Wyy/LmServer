package top.yeonon.lmserver.core.interceptor;

import top.yeonon.lmserver.core.annotation.Interceptor;

/**
 * Interceptor的适配器，和Filter一样，一般都是继承该类来实现拦截功能
 *
 * @Author yeonon
 * @date 2018/6/9 0009 15:29
 **/
public abstract class AbstractLmInterceptor implements LmInterceptor {
    /**
     * 排序的时候会默认使用这个方法
     *
     * @param o 需要比较的对象
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

