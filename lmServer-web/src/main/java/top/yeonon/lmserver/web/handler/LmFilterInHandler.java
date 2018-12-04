package top.yeonon.lmserver.web.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import top.yeonon.lmserver.web.filter.LmFilter;
import top.yeonon.lmserver.web.http.LmRequest;
import top.yeonon.lmserver.web.process.WebBeanProcessor;

import java.util.List;

/**
 * 处理Filter
 * @Author yeonon
 * @date 2018/6/10 0010 13:39
 **/
@ChannelHandler.Sharable
public class LmFilterInHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private LmFilterInHandler() {}

    public static final LmFilterInHandler INSTANCE = new LmFilterInHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        LmRequest request = LmRequest.build(ctx, fullHttpRequest);

        //获取请求路径
        String path = request.getPath();
        List<LmFilter> filters = WebBeanProcessor.getFilter(path);

        //遍历该Url对应的所有Filter，执行Filter逻辑
        if (filters != null) {
            for (LmFilter filter : filters) {
                filter.doFilter(request);
            }
        }
        //引用计数+1，因为Request还需要往下传递
        fullHttpRequest.retain();
        ctx.fireChannelRead(fullHttpRequest);
    }
}
