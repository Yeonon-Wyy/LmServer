package top.yeonon.lmserver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.core.ioc.discover.*;
import top.yeonon.lmserver.utils.PropertiesUtil;

/**
 * @Author yeonon
 * @date 2018/5/22 0022 22:27
 **/
public final class LmServerConfig {

    private static final Logger log = LoggerFactory.getLogger(LmServerConfig.class);

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

        BeanDiscover beanDiscover = new BeanDiscover();
        beanDiscover.doDiscover(scanPackage);

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
