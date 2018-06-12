package top.yeonon.lmserver.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import top.yeonon.lmserver.annotation.Interceptor;
import top.yeonon.lmserver.core.ioc.DefaultBeanProcessor;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;
import top.yeonon.lmserver.http.LmWebRequest;
import top.yeonon.lmserver.interceptor.LmInterceptor;

import javax.jws.WebResult;
import java.util.List;
import java.util.ListIterator;

/**
 * 后置拦截器的处理逻辑
 * @Author yeonon
 * @date 2018/6/10 0010 15:01
 **/
public class LmInterceptorOutHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof LmWebRequest) {
            LmWebRequest webRequest = (LmWebRequest) msg;
            LmRequest request = webRequest.getLmRequest();
            LmResponse response = webRequest.getLmResponse();
            List<LmInterceptor> interceptors = DefaultBeanProcessor.getInterceptor(request.getPath());

            //这里要反向遍历拦截器
            ListIterator<LmInterceptor> li = interceptors.listIterator();
            while (li.hasNext()) {
                li.next();
            }
            while (li.hasPrevious()) {
                li.previous().postHandler(request, response);
            }
        }

        //最后继续调用后面的ChannelOutboundHandler（如何有的话）
        super.write(ctx, msg, promise);
    }
}
