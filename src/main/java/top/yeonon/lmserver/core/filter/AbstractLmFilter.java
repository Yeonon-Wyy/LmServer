package top.yeonon.lmserver.core.filter;

import top.yeonon.lmserver.core.annotation.Filter;

/**
 * 过滤器适配器类，继承的时候只需要实现过滤逻辑即可，其他的例如过滤器顺序等适配器里都会实现
 * @Author yeonon
 * @date 2018/6/9 0009 14:58
 **/
public abstract class AbstractLmFilter implements LmFilter {

    /**
     * 排序的时候会默认使用这个方法
     * @param o
     * @return
     */
    @Override
    public int compareTo(LmFilter o) {
        Filter filter1 = this.getClass().getAnnotation(Filter.class);
        Filter filter2 = o.getClass().getAnnotation(Filter.class);
        if (filter1 == null || filter2 == null) {
            return -1;
        }
        Integer order1 = filter1.order();
        Integer order2 = filter2.order();

        return order1.compareTo(order2);
    }
}
