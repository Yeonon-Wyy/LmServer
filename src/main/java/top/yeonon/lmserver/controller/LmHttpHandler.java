package top.yeonon.lmserver.controller;

import jdk.internal.org.objectweb.asm.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.ioc.DefaultBeanProcessor;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        method.setAccessible(true);

        int paramSize = method.getParameterCount();

        Object[] args = new Object[paramSize];

        //使用ASM来获取参数名，做参数绑定
        putParams(args, paramSize, request, response);

        res = this.method.invoke(this.classInstance, args);
        return res;
    }

    private void putParams(Object[] args, int paramSize, LmRequest request, LmResponse response) {
        ClassReader reader = null;
        try {
            reader = new ClassReader(classInstance.getClass().getName());
            reader.accept(new ClassVisitor(ASM5) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (!method.getName().equals(name)) {
                        return super.visitMethod(access, name, desc, signature, exceptions);
                    }
                    return new MethodVisitor(ASM5) {
                        @Override
                        public void visitLocalVariable(String paramName, String typeName, String s2, Label label, Label label1, int i) {
                            //该方法会处理在方法里创建的本地变量，但是会先处理参数
                            //我们不想获取方法中创建的本地变量，故做此判断（i比索引大1，故需要 i >= paramSize+1）
                            if (i >= paramSize + 1) return;
                            //本地实例方法隐含了this,这里直接隐编码即可
                            if (paramName.equals("this")) return;
                            //String typeSimpleName = StringUtils.substring(s1, s1.lastIndexOf("/") + 1, s1.length() - 1);

                            if (REQUEST_TYPE_NAME.equals(typeName)) {
                                args[i - 1] = request;
                            } else if (RESPONSE_TYPE_NAME.equals(typeName)) {
                                args[i - 1] = response;
                            } else {

                                TypeNameEnum typeNameEnum = TypeNameEnum.getType(typeName);
                                //如果不是几个基本类型，那么就肯定是引用类型了，即对象
                                if (typeNameEnum == null) {
                                    String newTypeName = typeName.replace("L","").replace(";","").replaceAll("/",".");
                                    //目前仅支持有@Entity注解的类
                                    Class<?> type = DefaultBeanProcessor.get(newTypeName);
                                    if (type == null) {
                                        throw new IllegalArgumentException("不支持该类型");
                                    }
                                    //尝试组装对象
                                    Object object = processObjectParam(type, request);
                                    args[i-1] = object;
                                    return;
                                }
                                args[i - 1] = typeNameEnum.handle(paramName, request);
                            }


                        }
                    };
                }
            }, 0);
        } catch (IOException e) {
            log.error(e.getCause().toString());
        }

    }

    private static Object processObjectParam(Class<?> clz, LmRequest request) {
        try {
            Object instance = clz.newInstance();
            for (Field field : clz.getDeclaredFields()) {
                field.setAccessible(true);
                StringBuilder builder = new StringBuilder(field.getType().getName());
                //为了复用typeNameEnum，这里还需要修改一下从Java反射中得到的字段typename
                String newTypeName = builder.insert(0,'L').append(";").toString().replaceAll("\\.","/");
                TypeNameEnum typeNameEnum = TypeNameEnum.getType(newTypeName);
                if (typeNameEnum == null) {
                    //如果还是对象，即typeNameEnum里没有包含的，那么就递归调用processObjectParam
                    field.set(instance, processObjectParam(field.getType(), request));
                    return instance;
                }
                //为刚刚构造出来的实例设置对象
                field.set(instance, typeNameEnum.handle(field.getName(), request));
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
