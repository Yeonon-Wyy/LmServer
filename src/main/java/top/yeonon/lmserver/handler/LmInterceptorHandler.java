package top.yeonon.lmserver.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import top.yeonon.lmserver.core.ioc.DefaultBeanProcessor;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;
import top.yeonon.lmserver.interceptor.LmInterceptor;

import java.util.List;

/**
 * @Author yeonon
 * @date 2018/5/25 0025 16:17
 **/
public class LmInterceptorHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        LmRequest request = LmRequest.build(ctx, fullHttpRequest);

        String path = request.getPath();
        List<LmInterceptor> interceptors = DefaultBeanProcessor.getInterceptor(path);
        boolean isPass = true;
        if (interceptors == null) {
            ctx.fireChannelRead(msg);
        } else {
            LmResponse response = LmResponse.build(ctx, request);
            for (LmInterceptor interceptor : interceptors) {
                isPass = interceptor.doInterceptor(request, response);
                if (!isPass) break;
            }
            if (isPass)
                ctx.fireChannelRead(msg);
        }

    }
}
