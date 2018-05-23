package top.yeonon.lmserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.controller.ControllerDiscover;
import top.yeonon.lmserver.controller.HttpHandler;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * @Author yeonon
 * @date 2018/5/23 0023 19:14
 **/
public class LmHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger log = LoggerFactory.getLogger(LmHandler.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        LmRequest request = LmRequest.build(ctx, fullHttpRequest);
        LmResponse response = LmResponse.build(ctx, request);

        String path = request.getPath();
        HttpHandler handler = ControllerDiscover.getHandler(path.trim());
        if (handler != null) {
            Object message = handler.execute(request);
            if (message == null) {
                response.sendError("服务器异常或者参数错误", HttpResponseStatus.valueOf(500));
            } else {
                response.setContent(objectMapper.writeValueAsString(message))
                        .setContentType(LmResponse.ContentTypeValue.JSON_CONTENT)
                        .send();
            }

        } else {
            response.sendError("404 Not Found", HttpResponseStatus.NOT_FOUND);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
