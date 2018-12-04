package top.yeonon.lmserver.web.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import top.yeonon.lmserver.web.http.LmRequest;
import top.yeonon.lmserver.web.http.LmResponse;
import top.yeonon.lmserver.web.interceptor.LmInterceptor;
import top.yeonon.lmserver.web.process.WebBeanProcessor;

import java.util.List;

/**
 * @Author yeonon
 * @date 2018/5/25 0025 16:17
 **/
@ChannelHandler.Sharable
public class LmPreInterceptorHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public LmPreInterceptorHandler() {}

    public static final LmPreInterceptorHandler INSTANCE = new LmPreInterceptorHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        LmRequest request = LmRequest.build(ctx, fullHttpRequest);

        String path = request.getPath();
        List<LmInterceptor> interceptors = WebBeanProcessor.getInterceptor(path);
        //默认是通过拦截器
        boolean isPass = true;
        if (interceptors == null) {
            fullHttpRequest.retain();
            ctx.fireChannelRead(fullHttpRequest);
        } else {
            LmResponse response = LmResponse.build(ctx, request);
            for (LmInterceptor interceptor : interceptors) {
                isPass = interceptor.preHandler(request, response);
                //一旦有一个拦截器返回False，就没有必要执行之后的拦截器逻辑了
                if (!isPass) {
                    break;
                }
            }
            //通过才会继续往下执行业务逻辑
            if (isPass) {
                fullHttpRequest.retain();
                ctx.fireChannelRead(fullHttpRequest);
            }
        }
    }

}
