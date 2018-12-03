package top.yeonon.lmserver.web.databind;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.exception.LmServerParamErrorException;
import top.yeonon.lmserver.web.http.LmWebRequest;
import top.yeonon.lmserver.web.process.WebBeanProcessor;


import java.io.IOException;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * 基于ASM的参数绑定策略
 * @Author yeonon
 * @date 2018/11/25 0025 13:50
 **/
public class ASMParamBindStrategy extends AbstractParamBindStrategy {

    private static final Logger log = Logger.getLogger(ASMParamBindStrategy.class);

    public static final ASMParamBindStrategy INSTANCE = new ASMParamBindStrategy();

    protected static final String REQUEST_TYPE_NAME = "Ltop/yeonon/lmserver/http/LmRequest;";

    protected static final String RESPONSE_TYPE_NAME = "Ltop/yeonon/lmserver/http/LmResponse;";

    private ASMParamBindStrategy() {}


    /**
     * 采用ASM的技术来解析参数
     * @param args 参数数组
     * @param paramSize 参数数组长度
     * @param request 请求
     * @param method 方法实例
     * @param instance 该方法对应的类实例
     */
    @Override
    protected void putParams(Object[] args, int paramSize, LmWebRequest request, Method method, Object instance) {
        ClassReader reader = null;
        try {
            reader = new ClassReader(instance.getClass().getName());
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
                            if (i >= paramSize + 1) {
                                return;
                            }
                            //本地实例方法隐含了this,这里直接隐编码即可
                            if ("this".equals(paramName)) {
                                return;
                            }

                            if (REQUEST_TYPE_NAME.equals(typeName)) {
                                args[i - 1] = request.getLmRequest();
                            } else if (RESPONSE_TYPE_NAME.equals(typeName)) {
                                args[i - 1] = request.getLmResponse();
                            } else {

                                TypeNameEnum typeNameEnum = TypeNameEnum.getType(typeName);
                                //如果不是几个基本类型，那么就肯定是引用类型了，即对象
                                if (typeNameEnum == null) {
                                    String newTypeName = typeName.replace("L","").replace(";","").replaceAll("/",".");
                                    //目前仅支持有@Entity注解的类
                                    Class<?> type = WebBeanProcessor.getClassType(newTypeName);
                                    if (type == null) {
                                        throw new LmServerParamErrorException("不支持该类型");
                                    }
                                    //尝试组装对象，processObjectParam是抽象父类的方法
                                    Object object = processObjectParam(type, request.getLmRequest());
                                    args[i-1] = object;
                                    return;
                                }
                                args[i - 1] = typeNameEnum.handle(paramName, request.getLmRequest());
                            }


                        }
                    };
                }
            }, 0);
        } catch (IOException e) {
            log.error(e.getCause().toString());
        }
    }


}
