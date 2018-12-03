package top.yeonon.lmserver.web.annotation;

import top.yeonon.lmserver.core.annotation.Bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 服务
 * @Author yeonon
 * @date 2018/5/31 0031 19:40
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Service {

}
