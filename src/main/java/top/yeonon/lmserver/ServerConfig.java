package top.yeonon.lmserver;

import org.apache.commons.lang3.StringUtils;
import top.yeonon.lmserver.controller.ControllerDiscover;
import top.yeonon.lmserver.exception.ParamErrorException;
import top.yeonon.lmserver.utils.ClassUtil;
import top.yeonon.lmserver.utils.PropertiesUtil;

import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/5/22 0022 22:27
 **/
public class ServerConfig {

    private String scanBasePackages = PropertiesUtil.getStringProperty("scanBasePackages");

    public ServerConfig() {
        init();
    }

    private void init() {
        if (!StringUtils.isBlank(scanBasePackages)) {
            ControllerDiscover.doDiscover(scanBasePackages);
        } else {
            ControllerDiscover.doDiscover(".");
        }
    }
}
