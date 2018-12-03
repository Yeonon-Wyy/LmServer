package top.yeonon.lmserver.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.exception.EnvironmentException;
import top.yeonon.lmserver.web.handler.LmAfterInterceptorHandler;
import top.yeonon.lmserver.web.handler.LmFilterInHandler;
import top.yeonon.lmserver.web.handler.LmPreInterceptorHandler;
import top.yeonon.lmserver.web.handler.LmServerHandler;
import top.yeonon.lmserver.core.utils.JDKVersionUtil;

import java.net.InetSocketAddress;


/**
 * @Author yeonon
 * @date 2018/5/20 0020 17:51
 **/
public class LmServerStarter {

    private final static Logger log = Logger.getLogger(LmServerStarter.class);

    private Channel channel;
    private final EventLoopGroup group;
    private final ServerBootstrap serverBootstrap;
    private final int port;


    public LmServerStarter(int port) {
        this.port = port;
        group = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
    }

    private ChannelFuture start() {
        serverBootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpObjectAggregator(16 * 1024));


                        pipeline.addLast(new LmPreInterceptorHandler());
                        pipeline.addLast(new LmFilterInHandler());
                        pipeline.addLast(new LmServerHandler());
                        pipeline.addLast(new LmAfterInterceptorHandler());

                    }
                });
        ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(port));
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    private void stop() {
        if (channel != null) {
            channel.closeFuture();
        }
        group.shutdownGracefully();
    }

    public static void run(Class<?> mainClass) {

        checkEnvironment();

        LmServerConfig serverConfig = new LmServerConfig(mainClass);
        log.info("服务器监听端口 ： " + serverConfig.getServerPort());

        LmServerStarter starter = new LmServerStarter(serverConfig.getServerPort());
        ChannelFuture future = starter.start();

        log.info("启动成功");

        //添加关闭监听器
        Runtime.getRuntime().addShutdownHook(new Thread(starter::stop));

        future.channel().closeFuture().syncUninterruptibly();
    }

    /**
     * 检查环境条件是否可以启动应用
     * 比较常见就是JDK版本等
     */
    private static void checkEnvironment() {
        //检查JDK版本，因为项目是基于JDK1.8的，如果低版本的Java可能会出现问题
        if (!JDKVersionUtil.isGreaterJava8()) {
            throw new EnvironmentException("JDK版本必须不低于1.8");
        }
    }
}
