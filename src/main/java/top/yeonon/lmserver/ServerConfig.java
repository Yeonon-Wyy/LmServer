package top.yeonon.lmserver;

import org.apache.commons.lang3.StringUtils;
import top.yeonon.lmserver.exception.ParamErrorException;
import top.yeonon.lmserver.utils.ClassUtil;
import top.yeonon.lmserver.utils.PropertiesUtil;

import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/5/22 0022 22:27
 **/
public class ServerConfig {

    private final Set<Class<?>> classSet;
    private final String scanPackagePath;


    public ServerConfig() {
        this.scanPackagePath = PropertiesUtil.getStringProperty("scanPackagePath");
        if (StringUtils.isBlank(scanPackagePath)) {
            classSet = ClassUtil.getClassFromPackage(ServerConfig.class.getPackage().getName());
        }
        else {
            classSet = ClassUtil.getClassFromPackage(scanPackagePath);
        }
    }

    public Set<Class<?>> getClassSet() {
        return classSet;
    }

    public String getScanPackagePath() {
        return scanPackagePath;
    }
}
