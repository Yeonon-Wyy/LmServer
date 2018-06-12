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
public class LmInterceptorInHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        LmRequest request = LmRequest.build(ctx, fullHttpRequest);

        String path = request.getPath();
        List<LmInterceptor> interceptors = DefaultBeanProcessor.getInterceptor(path);
        //默认是通过拦截器
        boolean isPass = true;
        if (interceptors == null) {
            ctx.fireChannelRead(msg);
        } else {
            LmResponse response = LmResponse.build(ctx, request);
            for (LmInterceptor interceptor : interceptors) {
                isPass = interceptor.preHandler(request, response);
                if (!isPass) break; //一旦有一个拦截器返回False，就没有必要执行之后的拦截器逻辑了
            }
            if (isPass) //通过才会继续往下执行业务逻辑
                ctx.fireChannelRead(msg);
        }
    }

}
