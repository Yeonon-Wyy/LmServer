package top.yeonon.lmserver;


import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.ioc.AbstractBeanProcessor;
import top.yeonon.lmserver.core.ioc.BeanProcessor;
import top.yeonon.lmserver.core.ioc.DefaultBeanProcessor;
import top.yeonon.lmserver.utils.PropertiesUtil;

/**
 *
 * 系统配置类，用来配置系统默认属性
 * @Author yeonon
 * @date 2018/5/22 0022 22:27
 **/
public final class LmServerConfig {

    private static final Logger log = Logger.getLogger(LmServerConfig.class);

    private String scanPackage;
    private Integer serverPort;

    private final Class<?> mainClass;

    public LmServerConfig(Class<?> mainClass) {
        this.mainClass = mainClass;
        init();
    }

    private void init() {
        //处理属性值
        processProperties();

        //发现Bean
        new DefaultBeanProcessor(scanPackage).processBean();

        //扫描controller,interceptor,filter等

    }

    private void processProperties() {
        final String defaultPackage = mainClass.getPackage().getName();
        final Integer defaultServerPort = 9000;
        scanPackage = PropertiesUtil.getStringProperty("scanPackage", defaultPackage);
        serverPort = PropertiesUtil.getIntegerProperty("serverPort", defaultServerPort);
    }


    public Integer getServerPort() {
        return serverPort;
    }
}
