package top.yeonon.lmserver.controller;

import jdk.internal.org.objectweb.asm.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.ASM4;


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
        ClassReader reader = null;
        try {
            reader = new ClassReader(classInstance.getClass().getName());
            reader.accept(new ClassVisitor(ASM4) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (!method.getName().equals(name)) {
                        return super.visitMethod(access, name, desc, signature, exceptions);
                    }

                    return new MethodVisitor(ASM4) {

                        @Override
                        public void visitLocalVariable(String s, String s1, String s2, Label label, Label label1, int i) {
                            //该方法会处理在方法里创建的本地变量，但是会先处理参数
                            //我们不想获取方法中创建的本地变量，故做此判断（i比索引大1，故需要 i >= paramSize+1）
                            if (i >= paramSize+1) return;
                            if (s.equals("this")) return;
                            String typeSimpleName = StringUtils.substring(s1, s1.lastIndexOf("/") + 1, s1.length() - 1);

                            if (LmRequest.class.getSimpleName().equals(typeSimpleName)) {
                                args[i - 1] = request;
                            } else if (LmResponse.class.getSimpleName().equals(typeSimpleName)) {
                                args[i - 1] = response;
                            } else {
                                switch (typeSimpleName) {
                                    case "Integer":
                                        args[i - 1] = request.getIntegerParam(s);
                                        break;
                                    case "Long":
                                        args[i - 1] = request.getLongParam(s);
                                        break;
                                    case "String":
                                        args[i - 1] = request.getStringParam(s);
                                        break;
                                    case "Boolean":
                                        args[i - 1] = request.getBooleanParam(s);
                                        break;
                                    case "Float":
                                        args[i - 1] = request.getFloatParam(s);
                                        break;
                                    case "Double":
                                        args[i - 1] = request.getDoubleParam(s);
                                        break;
                                    default:
                                        args[i - 1] = request.getParam(s);
                                        break;
                                }
                            }
                        }
                    };
                }
            }, 0);
        } catch (IOException e) {
            log.error(e.getCause().toString());
        }

        res = this.method.invoke(this.classInstance, args);
        return res;
    }
}
