# LmServer

## 介绍
LmServer基于Netty实现的一个Web服务器，封装了Netty提供的HttpRequest和HttpResponse，暴露更易于使用的API。
也仿造了Spring的一些功能，例如基于注解的控制器Controller，依赖注入，也包含了过滤器，拦截器等功能，用户可根据具体的业务扩展接口。
最后想要说明一点，本项目仅仅是为了学习Netty，Spring等。

## 依赖
1. Netty 4.1.9
2. jackson 2.9.5
3. log4j 1.2.17

## 使用Demo
```java
@Controller
public class TestController {

    //自动注入Bean
    @Autowire
    private MyService myService;

    //这里的参数目前必须是LmRequest（还没有做数据绑定）
    @RequestMapping(value = "/test")
    public String test(LmRequest lmRequest) {
        return "test";
    }
}

@Interceptor(value = "/test")
public class MyInterceptor implements LmInterceptor {
    @Override
    public boolean doInterceptor(LmRequest request, LmResponse response) {
        response.setContent("被拦截了").send();
        return false;
    }
}

@Filter(value = "/test")
public class MyFilter implements LmFilter {

    @Override
    public void before(LmRequest request) {
        System.out.println("请求之前");
    }

    @Override
    public void after(LmResponse response) {
        System.out.println("请求之后");
    }
}



//启动类
public class Main {

    public static void main(String[] args) {
        LmServerStarter.run(Main.class);
    }
}

//普通类
public class TestBean {

    private String test = "testBean success!";


    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}

//配置类
@Configuration
public class MyConfig {

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setTest("我擦你妈比");
        return testBean;
    }
}

@Service
public class MyService implements IMyService{

    //这个TestBean类没有@Component注解，但已经在configuration类里添加了，所以也可以依赖注入进来
    @Autowire
    private TestBean testBean;

    public String testServeice() {
        return testBean.getTest();
    }
}
```

几点说明：
1. 拦截器和过滤器的注解值代表要拦截或者要过滤的路径，可以是多个，默认是不拦截或者不过滤，且必须要实现LmInterceptor或者LmFilter接口
2. 默认端口是9000，可以通过配置文件application.properties修改配置项serverPort修改
3. 因为注解是要发现的，所有需要包扫描。默认的包扫描路径就是启动类所在的包路径，当然，这也是可配置的。（配置项附在最后）
4. 建议启动类放在业务代码的包的根路径下
5. 目前支持的功能还很少，很多我想实现的功能还没有实现，主要是技术还不足，例如渲染界面，一直不知道该如何下手。


## 配置项
配置名  | 描述 | 默认值 | 重要级别
------ |---- | -------|--------
serverPort | 服务器监听的端口 | 9000 | 一般
scanPackage | 扫描Bean的包名 | 启动类所在跟路径 | 一般


### 待完成功能

名称 | 简单描述 | 重要级别 | 完成情况
------ |---- | -------|--------
渲染界面 | 后端完成数据处理将数据传递给渲染引擎渲染数据展示给用户 | 重要 | 0%
多线程扫描Bean | 目前还是单线程串行来扫描类，类多的话，可能会非常耗时，启动时间可能会比较长，影响用户体验，故打算支持多线来扫描类，并且做成可配置的 | 重要 | 0% 






