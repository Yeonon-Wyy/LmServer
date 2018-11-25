package top.yeonon.lmserver.databind.param.strategy;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import top.yeonon.lmserver.core.ioc.DefaultBeanProcessor;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmWebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


/**
 * @Author yeonon
 * @date 2018/11/25 0025 14:01
 **/
public class PrimevalParamStrategy extends AbstractParamBindStrategy {

    private static final Logger log = Logger.getLogger(PrimevalParamStrategy.class);

    protected static final String REQUEST_TYPE_NAME = "top.yeonon.lmserver.http.LmRequest";

    protected static final String RESPONSE_TYPE_NAME = "top.yeonon.lmserver.http.LmResponse";

    public static final PrimevalParamStrategy INSTANCE = new PrimevalParamStrategy();

    private PrimevalParamStrategy() {}

    @Override
    protected void putParams(Object[] args, int paramSize, LmWebRequest webRequest, Method method, Object instance) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < paramSize; i++) {

            //先处理两个特殊的对象 request和response
            String typeName = parameters[i].getType().getTypeName();
            if (typeName.equals(REQUEST_TYPE_NAME)) {
                args[i] = webRequest.getLmRequest();
                continue;
            } else if (typeName.equals(RESPONSE_TYPE_NAME)){
                args[i] = webRequest.getLmResponse();
                continue;
            }

            StringBuilder builder = new StringBuilder(typeName);
            //为了复用typeNameEnum，这里还需要修改一下从Java反射中得到的字段typename
            String newTypeName = builder.insert(0,'L').append(";").toString().replaceAll("\\.","/");
            TypeNameEnum typeNameEnum = TypeNameEnum.getType(newTypeName);
            if (typeNameEnum == null) {
                Class<?> type = DefaultBeanProcessor.getClassType(typeName);
                args[i] = processObjectParam(type, webRequest.getLmRequest());
            } else {
                //获取参数名称
                String paramName = parameters[i].getName();
                args[i] = typeNameEnum.handle(paramName, webRequest.getLmRequest());
            }
        }
    }
}
