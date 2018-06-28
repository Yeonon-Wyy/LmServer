package top.yeonon.lmserver.core.ioc;

/**
 * @Author yeonon
 * @date 2018/6/26 0026 18:26
 **/
public interface BeanProcessor {
    void beanProcessor(String packageName, boolean isMultiThread);
}
