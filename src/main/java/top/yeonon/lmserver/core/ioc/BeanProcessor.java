package top.yeonon.lmserver.core.ioc;

/**
 * Bean处理器接口
 * @Author yeonon
 * @date 2018/6/26 0026 18:26
 **/
public interface BeanProcessor {
    /**
     * Bean处理方法
     * @param packageName 包名
     * @param isMultiThread 是否使用多线程（因为多线程并不一定就快）
     */
    void beanProcessor(String packageName, boolean isMultiThread);
}
