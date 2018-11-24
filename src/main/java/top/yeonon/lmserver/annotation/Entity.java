package top.yeonon.lmserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yeonon
 * @date 2018/11/23 0023 15:49
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Entity {

}
