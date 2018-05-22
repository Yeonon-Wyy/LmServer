package top.yeonon.lmserver.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.CharsetUtil;
import top.yeonon.lmserver.http.HttpHeadersNames;
import top.yeonon.lmserver.http.LmRequest;

import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/5/21 0021 21:16
 **/
public class TestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        fullHttpRequest.headers().set(HttpHeadersNames.COOKIE, new DefaultCookie("my-name", "yeonon"));
        LmRequest lmRequest = LmRequest.build(ctx, fullHttpRequest);
        Map<String, Object> params = lmRequest.getParams();
        FullHttpResponse response = new DefaultFullHttpResponse(lmRequest.getProtocolVersion(), HttpResponseStatus.OK);
        System.out.println(lmRequest.isXWWWFORM());
        String message = "response : " + params.get("a");
        response.content().writeBytes(message.getBytes(CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, message.getBytes().length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");

        ctx.write(response);

        boolean isKeepAlive = lmRequest.isKeepAlive();
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!isKeepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
