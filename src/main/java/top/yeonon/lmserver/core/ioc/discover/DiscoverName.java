package top.yeonon.lmserver.core.ioc.discover;

/**
 * @Author yeonon
 * @date 2018/5/29 0029 21:32
 **/
public enum DiscoverName {
    CONTROLLER(0, "controller"),
    FILTER(1, "filter"),
    INTERCEPTOR(2, "interceptor");

    private int code;
    private String desc;

    DiscoverName(int code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
