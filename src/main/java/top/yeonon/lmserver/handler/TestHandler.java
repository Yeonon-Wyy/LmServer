package top.yeonon.lmserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.CharsetUtil;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;
import top.yeonon.lmserver.pojo.User;

import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/5/21 0021 21:16
 **/
public class TestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        String message = objectMapper.writeValueAsString(new User(1L, "user"));
        LmRequest lmRequest = LmRequest.build(ctx, fullHttpRequest);
        LmResponse lmResponse = LmResponse.build(ctx, lmRequest);
        ChannelFuture channelFuture = lmResponse.setContent(message)
                .addCookie(new DefaultCookie("my-name", "yeonon"))
                .addCookie(new DefaultCookie("my-class", "guocheng"))
                .setStatus(HttpResponseStatus.valueOf(400))
                .setContentType(LmResponse.ContentTypeValue.JSON_CONTENT)
                .send();

    }


}
