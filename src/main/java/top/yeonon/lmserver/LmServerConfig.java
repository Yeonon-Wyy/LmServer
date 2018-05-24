package top.yeonon.lmserver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.controller.LmControllerDiscover;
import top.yeonon.lmserver.filter.LmFilterDiscover;
import top.yeonon.lmserver.utils.PropertiesUtil;

/**
 * @Author yeonon
 * @date 2018/5/22 0022 22:27
 **/
public class LmServerConfig {

    private static final Logger log = LoggerFactory.getLogger(LmServerConfig.class);

    private String controllerPackage = PropertiesUtil.getStringProperty("controllerPackage");
    private String filterPackage = PropertiesUtil.getStringProperty("filterPackage");
    private Integer serverPort = PropertiesUtil.getIntegerProperty("serverPort");

    private final Class<?> mainClass;

    public LmServerConfig(Class<?> mainClass) {
        this.mainClass = mainClass;
        init();
    }

    private void init() {

        processBasePackages();
        processFilterPackages();
        processServerPort();

    }

    /**
     * 处理扫描filter的策略，如果用户配置了要扫描的路径，就使用用户配置的，如果没有配置，就以启动类所在包开始扫描
     */
    private void processFilterPackages() {
        if (!StringUtils.isBlank(filterPackage)) {
            LmFilterDiscover.doDiscover(filterPackage);
        } else {
            LmFilterDiscover.doDiscover(mainClass.getPackage().getName());
        }
    }

    /**
     * 处理扫描controller的策略，如果用户配置了要扫描的路径，就使用用户配置的，如果没有配置，就以启动类所在包开始扫描
     */
    private void processBasePackages() {
        if (!StringUtils.isBlank(controllerPackage)) {
            LmControllerDiscover.doDiscover(controllerPackage);
        } else {
            LmControllerDiscover.doDiscover(mainClass.getPackage().getName());
        }
    }

    /**
     * 处理服务器监听的端口
     */
    private void processServerPort() {
        if (serverPort == null)
            serverPort = 9000;
        log.info("服务器运行在 " + serverPort + " 端口上");
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public Integer getServerPort() {
        return serverPort;
    }
}
