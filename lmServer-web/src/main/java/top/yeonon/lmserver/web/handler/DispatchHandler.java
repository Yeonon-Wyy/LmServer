package top.yeonon.lmserver.web.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import top.yeonon.lmserver.web.http.LmRequest;
import top.yeonon.lmserver.web.http.LmResponse;
import top.yeonon.lmserver.web.process.WebBeanProcessor;

/**
 * @Author yeonon
 * @date 2018/12/4 0004 12:33
 **/
@ChannelHandler.Sharable
public class DispatchHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private DispatchHandler() {}

    public static final DispatchHandler INSTANCE = new DispatchHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        LmRequest lmRequest = LmRequest.build(ctx, fullHttpRequest);
        String path = lmRequest.getPath();
        LmRequest.LMHttpMethod method = lmRequest.getMethod();
        if (WebBeanProcessor.getMethodAndMethodHandlerOfPath(path.trim()) == null) {
            sendNotFoundError(ctx, lmRequest);
            return;
        } else if (WebBeanProcessor.getHandler(path.trim(), method) == null) {
            sendBadRequestError(ctx, lmRequest);
            return;
        }
        fullHttpRequest.retain();
        ctx.fireChannelRead(fullHttpRequest);
    }

    private void sendBadRequestError(ChannelHandlerContext ctx, LmRequest request) {
        LmResponse response = LmResponse.build(ctx, request);
        response.setContentType(LmResponse.ContentTypeValue.PLAIN_CONTENT)
                .sendError("不支持该请求方法", HttpResponseStatus.BAD_REQUEST);
    }

    private void sendNotFoundError(ChannelHandlerContext ctx, LmRequest request) {
        LmResponse response = LmResponse.build(ctx, request);
        response.setContentType(LmResponse.ContentTypeValue.PLAIN_CONTENT)
                .sendError("请求路径不存在！", HttpResponseStatus.NOT_FOUND);
    }
}
