package top.yeonon.lmserver.http;

/**
 *
 * Netty提供的HttpHeaderNames 有一些大小写问题，转换有些不雅
 *
 * @Author yeonon
 * @date 2018/5/22 0022 17:41
 **/
public interface HttpHeadersNames {

    String CONTENT_TYPE = "Content-Type";

    String ORIGIN = "Origin";

    String ACCEPT = "Accept";

    String COOKIE = "cookie";

    String CONNECTION = "Connection";

    String USER_AGENT = "User-Agent";

    String HOST = "Host";

    String ACCEPT_ENCODING = "Accept-Encoding";

    String ACCEPT_LANGUAGE = "Accept-Language";

    String CONTENT_LENGTH = "Content-Length";

}
