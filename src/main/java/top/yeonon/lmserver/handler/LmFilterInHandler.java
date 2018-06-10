package top.yeonon.lmserver.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import top.yeonon.lmserver.core.ioc.DefaultBeanProcessor;
import top.yeonon.lmserver.filter.LmFilter;
import top.yeonon.lmserver.http.LmRequest;

import java.util.List;

/**
 * @Author yeonon
 * @date 2018/6/10 0010 13:39
 **/
public class LmFilterInHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        LmRequest request = LmRequest.build(ctx, fullHttpRequest);

        String path = request.getPath();
        List<LmFilter> filters = DefaultBeanProcessor.getFilter(path);

        if (filters != null) {
            for (LmFilter filter : filters) {
                filter.doFilter(request);
            }
        }


        super.channelRead(ctx, msg);
    }
}
