package top.yeonon.lmserver.core.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/5/20 0020 20:26
 **/
public class LmRequest {

    private FullHttpRequest nettyRequest;


    private String path;
    private String ip;

    private Map<String, Object> params = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, Cookie> cookies = new HashMap<>();


    private LmRequest(ChannelHandlerContext ctx, FullHttpRequest nettyRequest) {
        this.nettyRequest = nettyRequest;
        final String uri = nettyRequest.uri();
        this.putHeadersAndCookies(nettyRequest.headers());
        this.putParams(new QueryStringDecoder(uri));

        //可能会是Post请求（Post请求也有可能带有Query参数，故Query参数是必须要获取的）
        if (nettyRequest.method() != HttpMethod.GET) {
            HttpPostRequestDecoder postRequestDecoder = null;
            try {
                postRequestDecoder = new HttpPostRequestDecoder(nettyRequest);
                this.putParams(postRequestDecoder);
            } finally {
                if (postRequestDecoder != null) {
                    postRequestDecoder.destroy();
                }
            }
        }

        this.putIp(ctx);
        this.putPath(uri);
    }


    /**
     * 设置路径
     */
    private void putPath(final String uri) {
        int index = uri.indexOf("?");
        if (index < 0) {
            this.path = uri;
        } else {
            this.path = uri.substring(0, index);
        }
    }

    /**
     * 设置Ip
     *
     * @param ctx channelHandler上下文
     */
    private void putIp(ChannelHandlerContext ctx) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        this.ip = address.getAddress().getHostAddress();
    }


    //参数相关内容

    /**
     * 从uri中解析Query 参数
     *
     * @param queryStringDecoder 参数解码器
     */
    protected void putParams(QueryStringDecoder queryStringDecoder) {
        if (queryStringDecoder != null) {
            Map<String, List<String>> parameters = queryStringDecoder.parameters();
            for (String paramName : parameters.keySet()) {
                if (parameters.get(paramName).size() == 1) {
                    params.put(paramName, parameters.get(paramName).get(0));
                }
            }
        }
    }

    /**
     * 支持Post请求
     *
     * @param decoder 专门解决Post请求的参数
     */
    protected void putParams(HttpPostRequestDecoder decoder) {
        if (decoder == null) {
            return;
        }

        for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
            putParam(data);
        }
    }

    /**
     * 参数绑定用的
     *
     * @param data
     */
    protected void putParam(InterfaceHttpData data) {
        InterfaceHttpData.HttpDataType dataType = data.getHttpDataType();
        if (dataType == InterfaceHttpData.HttpDataType.Attribute) {
            //普通的参数
            Attribute attribute = (Attribute) data;
            try {
                this.putParam(attribute.getName(), attribute.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 直接填加参数
     *
     * @param key 参数名
     * @param val 参数值
     */
    protected void putParam(String key, Object val) {
        this.params.put(key, val);
    }

    /**
     * @return 参数的Map
     */
    public Map<String, Object> getParams() {
        return this.params;
    }

    /**
     * 根据参数名获取对应参数
     *
     * @param paramName 参数名
     * @return 参数值
     */
    public Object getParam(String paramName) {
        return params.get(paramName);
    }


    /**
     * 获取String类型的参数值
     *
     * @param paramName 参数名
     * @return String类型参数值
     */
    public String getStringParam(String paramName) {
        Object paramValue = this.getParam(paramName);
        return (String) paramValue;
    }

    /**
     * 获取Double类型的值
     *
     * @param paramName 参数名
     * @return Double类型的参数值
     */
    public Double getDoubleParam(String paramName) {
        String paramValue = this.getStringParam(paramName);
        if (paramValue == null) {
            return null;
        }
        return Double.valueOf(paramValue);
    }

    /**
     * 获取Integer类型的值
     *
     * @param paramName 参数名
     * @return Integer类型的参数值
     */
    public Integer getIntegerParam(String paramName) {
        String paramValue = this.getStringParam(paramName);
        if (paramValue == null) {
            return null;
        }
        return Integer.valueOf(paramValue);
    }

    /**
     * 获取Long类型的值
     *
     * @param paramName 参数名
     * @return Long类型的参数值
     */
    public Long getLongParam(String paramName) {
        String paramValue = this.getStringParam(paramName);
        if (paramValue == null) {
            return null;
        }
        return Long.valueOf(paramValue);
    }

    /**
     * 获取Float类型的值
     *
     * @param paramName 参数名
     * @return Float类型的参数值
     */
    public Float getFloatParam(String paramName) {
        String paramValue = this.getStringParam(paramName);
        if (paramValue == null) {
            return null;
        }
        return Float.valueOf(paramValue);
    }

    /**
     * 获取Boolean类型的值
     *
     * @param paramName 参数名
     * @return Boolean类型的参数值
     */
    public Boolean getBooleanParam(String paramName) {
        String paramValue = this.getStringParam(paramName);
        if (paramValue == null) {
            return null;
        }
        return Boolean.valueOf(paramValue);
    }

    /**
     * 获取Byte类型的值
     * @param paramName 参数名
     * @return 值
     */
    public Byte getByteParam(String paramName) {
        String paramValue = this.getStringParam(paramName);
        if (paramValue == null) {
            return null;
        }
        return Byte.valueOf(paramValue);
    }

    /**
     * 获取Short类型的参数值
     * @param paramName 参数名
     * @return 值
     */
    public Short getShortParam(String paramName) {
        String paramValue = this.getStringParam(paramName);
        if (paramValue == null) {
            return null;
        }
        return Short.valueOf(paramValue);
    }

    /**
     * 获取请求路径
     *
     * @return 路径
     */
    public String getPath() {
        return this.path;
    }


    // 请求头相关(包含cookie)

    /**
     * 从Netty提供的HttpHeaders里提取出header和cookie
     *
     * @param headers netty 提供的headers 包含诸多信息
     */
    public void putHeadersAndCookies(HttpHeaders headers) {
        //填充头部
        for (Map.Entry<String, String> entry : headers) {
            this.headers.put(entry.getKey(), entry.getValue());
        }

        final String cookieString = this.headers.get(HttpHeaderNames.COOKIE.toString());
        if (StringUtils.isNotBlank(cookieString)) {
            final Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieString);
            for (Cookie cookie : cookies) {
                this.cookies.put(cookie.name(), cookie);
            }
        }
    }

    /**
     * @return headers Map
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * 根据Key 获取 Value
     *
     * @param headName 请求头字段名称
     * @return 请求头字段名称对应的值
     */
    public String getHeader(String headName) {
        return this.headers.get(headName);
    }


    //实用API

    /**
     * 判断是否是长连接
     *
     * @return 是否是长连接
     */
    public boolean isKeepAlive() {
        final String connection = getHeader(HttpHeaderNames.CONNECTION.toString());

        //connection为false，即应该是关闭的，故无论如何（即使确实是长连接），都要返回false
        if (HttpHeaderValues.CLOSE.toString().equalsIgnoreCase(connection)) {
            return false;
        }

        //Http1.0 没有长连接，直接返回False
        if (HttpVersion.HTTP_1_0.text().equalsIgnoreCase(getProtocolVersion().toString())) {
            return false;
        }

        //Http1.1 以上默认就是长连接
        return true;
    }

    public boolean isXWwwFormUrlencoded() {

        String contentTypeValue = this.getHeader("Content-Type");
        if (StringUtils.isBlank(contentTypeValue)) {
            return false;
        }
        return contentTypeValue.
                equalsIgnoreCase(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString());
    }


    /**
     * 获取Http协议版本号
     *
     * @return 版本号
     */
    public HttpVersion getProtocolVersion() {
        return nettyRequest.protocolVersion();
    }

    /**
     * 获取Http协议版本号的字符串形式
     *
     * @return 字符串形式的版本号
     */
    public String getProtocolVersionString() {
        return this.getProtocolVersion().text();
    }

    /**
     * 获取请求方法
     *
     * @return 请求方法
     */
    public LMHttpMethod getMethod() {
        return LMHttpMethod.getMethod(nettyRequest.method().name());
    }

    /**
     * 获取请求方法
     *
     * @return 获取请求方法字符形式
     */
    public String getMethodString() {
        return this.getMethod().toString();
    }


    /**
     * 请求方法枚举类（Netty的HttpMethod不是枚举，无法用在注解中）
     */
    public enum  LMHttpMethod {
        GET("GET"),POST("POST"),PUT("PUT"),DELETE("DELETE"),
        OPTIONS("OPTIONS"),HEAD("HEAD"),PATCH("PATCH"),
        TRACE("TRACE"),CONNECT("CONNECT");


        private String name;

        LMHttpMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static LMHttpMethod getMethod(String name) {
            for (LMHttpMethod method : values()) {
                if (method.getName().equals(name)) {
                    return method;
                }
            }
            return null;
        }
    }

/**-----------------------------------------------------------------------------------------------------------------------------*/


    /**
     * 构建LmRequest
     *
     * @param ctx          ChannelHandlerContext
     * @param nettyRequest FullHttpRequest
     * @return LmRequest
     */
    public static LmRequest build(ChannelHandlerContext ctx, FullHttpRequest nettyRequest) {
        return new LmRequest(ctx, nettyRequest);
    }


}
