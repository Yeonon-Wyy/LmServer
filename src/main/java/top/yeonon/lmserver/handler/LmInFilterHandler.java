package top.yeonon.lmserver.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import top.yeonon.lmserver.filter.LmFilter;
import top.yeonon.lmserver.filter.LmFilterDiscover;
import top.yeonon.lmserver.http.LmRequest;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 16:19
 **/
public class LmInFilterHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        LmRequest request = LmRequest.build(ctx, fullHttpRequest);
        String path = request.getPath();
        LmFilter filter = LmFilterDiscover.getFilter(path);

        if (filter != null) {
            filter.before(request);
        }
        ctx.fireChannelRead(msg);

    }
}
