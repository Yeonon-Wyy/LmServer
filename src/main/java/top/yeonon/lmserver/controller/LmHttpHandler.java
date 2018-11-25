package top.yeonon.lmserver.controller;

import jdk.internal.org.objectweb.asm.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.ioc.DefaultBeanProcessor;
import top.yeonon.lmserver.databind.param.strategy.ASMParamBindStrategy;
import top.yeonon.lmserver.databind.param.strategy.PrimevalParamStrategy;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;
import top.yeonon.lmserver.http.LmWebRequest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ASM4;
import static org.objectweb.asm.Opcodes.ASM5;


/**
 * 处理请求的类
 * <p>
 * Ps： 一个类可能有多个处理Url的方法，即一个classInstance可能有多个method，这种情况也会有多个handler，
 * 但是他们的method字段不同(classInstance相同)。
 *
 * @Author yeonon
 * @date 2018/5/23 0023 18:24
 **/
public class LmHttpHandler {
    //类的一个实例
    private Object classInstance;

    //映射的方法
    private Method method;

    private static final String REQUEST_TYPE_NAME = "Ltop/yeonon/lmserver/http/LmRequest;";

    private static final String RESPONSE_TYPE_NAME = "Ltop/yeonon/lmserver/http/LmResponse;";

    private static final Logger log = Logger.getLogger(LmHttpHandler.class);

    /**
     * @param classInstance 实例
     * @param method        处理映射的方法
     */
    public LmHttpHandler(Object classInstance, Method method) {
        this.classInstance = classInstance;
        this.method = method;
    }

    /**
     * 调用方法,并产生返回值
     *
     * @param request 这个参数是必须的
     * @return 调用method.invoke()的返回值
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     */
    public Object execute(LmRequest request, LmResponse response) throws InvocationTargetException, IllegalAccessException {
        Object res = null;
//        Object[] args = ASMParamBindStrategy.INSTANCE.execute(method, classInstance, new LmWebRequest(request, response));
        Object[] args = PrimevalParamStrategy.INSTANCE.execute(method, classInstance, new LmWebRequest(request, response));
        res = this.method.invoke(this.classInstance, args);
        return res;
    }
}
