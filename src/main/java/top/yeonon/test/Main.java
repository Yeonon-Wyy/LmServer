package top.yeonon.test;

import top.yeonon.lmserver.core.LmServerStarter;

/**
 * @Author yeonon
 * @date 2018/11/28 0028 17:58
 **/
public class Main {
    public static void main(String[] args) throws NoSuchMethodException {
        LmServerStarter.run(Main.class);

//        Method method = TestController.class.getDeclaredMethod("test", Long.class);
//        Set<Pair<LmRequest.LMHttpMethod, MethodHandler>> set;
//        Pair<LmRequest.LMHttpMethod, MethodHandler> pair1 =
//                new Pair<>(LmRequest.LMHttpMethod.POST, new DefaultMethodHandler(TestController.class, method));
//        Pair<LmRequest.LMHttpMethod, MethodHandler> pair2 =
//                new Pair<>(LmRequest.LMHttpMethod.POST, new DefaultMethodHandler(TestController.class, method));
//        System.out.println(pair1.equals(pair2));
    }
}
