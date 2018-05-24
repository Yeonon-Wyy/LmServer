package top.yeonon.lmserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.controller.LmControllerDiscover;
import top.yeonon.lmserver.controller.LmHttpHandler;
import top.yeonon.lmserver.filter.LmFilter;
import top.yeonon.lmserver.filter.LmFilterDiscover;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * 属于Netty框架下的的handler，处于进站方向的最后一个
 * @Author yeonon
 * @date 2018/5/23 0023 19:14
 **/
public class LmServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger log = LoggerFactory.getLogger(LmServerHandler.class);

    //将对象转换成JSON格式的字符串需要用的
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        //构建LmRequest和LmResponse
        LmRequest request = LmRequest.build(ctx, fullHttpRequest);
        LmResponse response = LmResponse.build(ctx, request);

        //获取请求辣眼睛
        String path = request.getPath();
        LmHttpHandler handler = LmControllerDiscover.getHandler(path.trim());
        LmFilter filter = LmFilterDiscover.getFilter(path);

        //执行前置filter
        doBeforeFilter(filter, request);

        if (handler != null) {
            //handler不为null的话，就正常执行url对应的处理方法，并将返回值写回客户端
            Object message = handler.execute(request);
            if (message == null) {
                //如果消息为null，也许是参数错误，或者服务端出现异常，例如读写数据库异常等
                response.sendError("服务器异常或者参数错误", HttpResponseStatus.valueOf(500));
            } else {
                response.setContent(objectMapper.writeValueAsString(message))
                        .setContentType(LmResponse.ContentTypeValue.JSON_CONTENT)
                        .send();
            }
        } else {
            //handler为null，即没有url映射，这个时候应该返回404错误
            response.sendError("404 Not Found", HttpResponseStatus.NOT_FOUND);
        }

        //执行后置filter
        doAfterFilter(filter, response);

    }

    /**
     * 在执行业务逻辑之前执行
     * @param filter 过滤器
     * @param lmRequest 请求
     */
    private void doBeforeFilter(LmFilter filter, LmRequest lmRequest) {
        if (filter == null) {
            return;
        }
        filter.before(lmRequest);
    }

    /**
     * 在执行业务逻辑之后
     * @param filter 过滤器
     * @param lmResponse 响应
     */
    private void doAfterFilter(LmFilter filter, LmResponse lmResponse) {
        if (filter == null) {
            return;
        }
        filter.after(lmResponse);
    }

    /**
     * 异常捕获，在Netty中，这应该是Pipeline中最后的Handler实现的方法
     * @param ctx ChannelHandlerContext
     * @param cause Throwable
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
