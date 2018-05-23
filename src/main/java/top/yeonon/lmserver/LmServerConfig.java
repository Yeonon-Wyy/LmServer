package top.yeonon.lmserver;

import org.apache.commons.lang3.StringUtils;
import top.yeonon.lmserver.controller.LmControllerDiscover;
import top.yeonon.lmserver.utils.PropertiesUtil;

/**
 * @Author yeonon
 * @date 2018/5/22 0022 22:27
 **/
public class LmServerConfig {

    private String scanBasePackages = PropertiesUtil.getStringProperty("scanBasePackages");
    private Integer serverPort = PropertiesUtil.getIntegerProperty("serverPort");


    public LmServerConfig() {
        init();
    }

    private void init() {

        processBasePackages();
        processServerPort();

    }

    private void processBasePackages() {
        if (!StringUtils.isBlank(scanBasePackages)) {
            LmControllerDiscover.doDiscover(scanBasePackages);
        } else {
            LmControllerDiscover.doDiscover(".");
        }
    }

    private void processServerPort() {
        if (serverPort == null)
            serverPort = 9000;
    }

    public String getScanBasePackages() {
        return scanBasePackages;
    }

    public Integer getServerPort() {
        return serverPort;
    }
}
