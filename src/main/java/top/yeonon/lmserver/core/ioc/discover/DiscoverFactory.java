package top.yeonon.lmserver.core.ioc.discover;

/**
 * @Author yeonon
 * @date 2018/5/29 0029 21:28
 **/
public class DiscoverFactory {

    private static Discover discover;

    public static Discover getDiscover(int discoverCode) {
        if (discoverCode == DiscoverName.CONTROLLER.getCode()) {
            discover = new LmControllerDiscover();
        } else if (discoverCode == DiscoverName.FILTER.getCode()) {
            discover = new LmFilterDiscover();
        } else if (discoverCode == DiscoverName.INTERCEPTOR.getCode()) {
            discover = new LmInterceptorDiscover();
        } else {
            discover = null;
        }
        return discover;
    }
}
