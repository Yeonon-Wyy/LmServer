package top.yeonon.lmserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 16:23
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Filter {
    String[] value();

    int order() default 0;

}
