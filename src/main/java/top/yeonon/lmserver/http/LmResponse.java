package top.yeonon.lmserver.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/5/20 0020 20:26
 **/
public class LmResponse {

    private static final Logger log = Logger.getLogger(LmResponse.class);

    /**
     * Content-Type的值常量
     */
    public interface ContentTypeValue {
        //内容类型是HTML
        String HTML_CONTENT = "text/html;charset=utf-8";
        //内容类型是纯文本
        String PLAIN_CONTENT = "text/plain;charset=utf-8";
        //内容类型是JSON
        String JSON_CONTENT = "application/json;charset=utf-8";
        //内容类型是XML
        String XML_CONTENT = "text/xml;charset=utf-8";
        //内容类型是JavaScript脚本
        String JAVASCRIPT_CONTENT = "application/javascript;charset=utf-8";
    }


    private Object content = Unpooled.EMPTY_BUFFER;
    private HttpHeaders headers = new DefaultHttpHeaders();
    private Set<Cookie> cookies = new HashSet<>();
    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private HttpResponseStatus status = HttpResponseStatus.OK;
    private String contentType = ContentTypeValue.PLAIN_CONTENT;


    private ChannelHandlerContext ctx;
    private LmRequest lmRequest;
    private boolean isSent;

    private LmResponse(ChannelHandlerContext ctx, LmRequest lmRequest) {
        this.ctx = ctx;
        this.lmRequest = lmRequest;

        //默认添加一些请求头
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }


    /**
     * 设置Content-Type
     *
     * @param contentType 字符串
     */
    public LmResponse setContentType(String contentType) {
        this.contentType = contentType;
        headers.set(HttpHeaderNames.CONTENT_TYPE, this.contentType);
        return this;
    }

    /**
     * 设置响应状态码
     *
     * @param status 状态码
     * @return 本身
     */
    public LmResponse setStatus(HttpResponseStatus status) {
        this.status = status;
        return this;
    }

    /**
     * 设置响应状态码（使用数字，例如200）
     *
     * @param statusCode 响应码的数字表示
     * @return 本身
     */
    public LmResponse setStatus(int statusCode) {
        this.status = HttpResponseStatus.valueOf(statusCode);
        return this;
    }

    /**
     * 设置响应头字段（重复的会覆盖值）
     *
     * @param headerName  字段名
     * @param headerValue 字段值
     * @return 本身
     */
    public LmResponse setHeaders(String headerName, String headerValue) {
        this.headers.set(headerName, headerValue);
        return this;
    }

    /**
     * 设置响应头字段
     *
     * @param headerName  字段名
     * @param headerValue 字段值
     * @return 本身
     */
    public LmResponse addHeaders(String headerName, String headerValue) {
        this.headers.add(headerName, headerValue);
        return this;
    }

    /**
     * 设置响应头集合
     *
     * @param headers 用户可以自己构造HttpHeaders，然后一次传递进来
     * @return 本身
     */
    public LmResponse addHeaders(HttpHeaders headers) {
        this.headers.add(headers);
        return this;
    }

    /**
     * 设置Content-Length
     *
     * @param contentLength 长度
     * @return 本身
     */
    public LmResponse setContentLength(long contentLength) {
        this.headers.set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
        return this;
    }

    /**
     * 设置长连接
     *
     * @return 本身
     */
    public LmResponse setKeepAlive() {
        this.headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return this;
    }


    /**
     * 设置Http版本号，目前默认是Http1.1，最新是2.0
     * @param version
     * @return
     */
    public LmResponse setHttpVersion(HttpVersion version) {
        this.httpVersion = version;
        return this;
    }

    /**
     * 添加cookie
     *
     * @param cookie 标准cookie对象
     * @return 本身
     */
    public LmResponse addCookie(Cookie cookie) {
        this.cookies.add(cookie);
        return this;
    }

    /**
     * 添加cookie
     *
     * @param cookieName  字段名
     * @param cookieValue 字段值
     * @return 本身
     */
    public LmResponse addCookie(String cookieName, String cookieValue) {
        this.addCookie(new DefaultCookie(cookieName, cookieValue));
        return this;
    }

    /**
     * 设置返回文本内容，基于Netty，故使用ByteBuf包装
     *
     * @param content 返回内容
     * @return 本身
     */
    public LmResponse setContent(String content) {
        this.content = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        this.setContentType(ContentTypeValue.PLAIN_CONTENT);
        return this;
    }

    /**
     * 设置返回的Buffer
     *
     * @param content ByteBuf类型
     * @return 本身
     */
    public LmResponse setContent(ByteBuf content) {
        this.content = content;
        this.setContentType(ContentTypeValue.JSON_CONTENT);
        return this;
    }

    /**
     * 设置返回的Buffer
     *
     * @param content ByteBuf类型
     * @return 本身
     */
    public LmResponse setContent(byte[] content) {
        this.content = Unpooled.copiedBuffer(content);
        this.setContentType(ContentTypeValue.JSON_CONTENT);
        return this;
    }

    public LmResponse setContent(File file) {
        this.content = file;
        return this;
    }


    /**
     * 转换成Netty支持的Response
     *
     * @return response
     */
    private FullHttpResponse toFullHttpResponse() {
        ByteBuf buf = (ByteBuf) content;
        final FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpVersion, status, buf);

        //设置Headers
        final HttpHeaders httpHeaders = fullHttpResponse.headers();
        setContentLength(buf.readableBytes());
        httpHeaders.add(this.headers);

        //设置cookies
        for (Cookie cookie : this.cookies) {
            httpHeaders.add(HttpHeaderNames.SET_COOKIE.toString(), ServerCookieEncoder.LAX.encode(cookie));
        }

        return fullHttpResponse;
    }

    private HttpResponse toDefaultHttpResponse() {
        final HttpResponse httpResponse = new DefaultHttpResponse(httpVersion, status);
        //设置Headers
        final HttpHeaders httpHeaders = httpResponse.headers();
        httpHeaders.add(this.headers);

        //设置cookies
        for (Cookie cookie : this.cookies) {
            httpHeaders.add(HttpHeaderNames.SET_COOKIE.toString(), ServerCookieEncoder.LAX.encode(cookie));
        }
        return httpResponse;
    }

    /**
     * 向客户端发送消息
     *
     * @return ChannelFuture
     */
    public ChannelFuture send() {
        ChannelFuture future = null;
        if (content instanceof File) {
            File file = (File) content;
            try {
                future = sendFile(file);
            } catch (IOException e) {
                log.error(e.toString());
            }
        } else {
            future = sendFull();
        }
        this.isSent = true;
        return future;
    }


    /**
     * 传输文件，在Netty中，传输File需要做特殊处理，例如要用HttpResponse作为响应而不是FullHttpResponse
     * @param file  文件
     * @return future
     * @throws IOException
     */
    private ChannelFuture sendFile(File file) throws IOException {
        if (lmRequest.isKeepAlive()) {
            setKeepAlive();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();
        setContentLength(fileLength);
        ctx.write(toDefaultHttpResponse());
        ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength));
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!lmRequest.isKeepAlive()) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        return future;
    }

    /**
     * 发送文本内容给客户端（文本内容一般使用FullHttpResponse）
     *
     * @return ChannelFuture
     */
    private ChannelFuture sendFull() {
        if (lmRequest.isKeepAlive()) {
            setKeepAlive();
        }
        ctx.write(toFullHttpResponse());
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        if (!lmRequest.isKeepAlive()) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

        return future;
    }

    /**
     * 发送错误信息，响应码给前端，最后要关闭channel
     * @param errMsg
     * @param status
     * @return
     */
    public ChannelFuture sendError(String errMsg, HttpResponseStatus status) {
        this.setContent(errMsg);
        setStatus(status);
        ctx.write(toFullHttpResponse());
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        //错误了应该是要关闭channel的
        future.addListener(ChannelFutureListener.CLOSE);
        return future;
    }

    /**
     * 判断该Response是否已发送，防止重复发送
     * @return
     */
    public boolean isSent() {
        return isSent;
    }


    /**
     * 构建LmResponse
     * @param ctx ChannelHandlerContext
     * @param lmRequest requiest
     * @return response
     */
    public static LmResponse build(ChannelHandlerContext ctx, LmRequest lmRequest) {
        return new LmResponse(ctx, lmRequest);
    }


}
