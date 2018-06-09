package top.yeonon.lmserver.filter;

import top.yeonon.lmserver.annotation.Filter;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * @Author yeonon
 * @date 2018/6/9 0009 14:58
 **/
public class AbstractLmFilter implements LmFilter {


    @Override
    public void before(LmRequest request) {

    }

    @Override
    public void after(LmResponse response) {

    }

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
