package top.yeonon.lmserver.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import top.yeonon.lmserver.core.ioc.discover.BeanDiscover;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;
import top.yeonon.lmserver.interceptor.LmInterceptor;

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
        LmInterceptor interceptor = BeanDiscover.getInterceptor(path);
        boolean isPass = true;
        if (interceptor == null) {
            ctx.fireChannelRead(msg);
        } else {
            LmResponse response = LmResponse.build(ctx, request);
            isPass = interceptor.doInterceptor(request, response);
            if (isPass)
                ctx.fireChannelRead(msg);
        }

    }
}
