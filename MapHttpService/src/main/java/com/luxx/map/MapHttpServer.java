package com.luxx.map;

import com.luxx.map.service.HttpServerInboundHandler;
import com.luxx.map.service.MapDbOperation;
import com.luxx.map.util.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map服务，主程序
 *
 * @author luxiaoxun
 * @version 1.0
 * @since 2015.06.1
 */
public class MapHttpServer {
    private static Logger log = LoggerFactory.getLogger(MapHttpServer.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private EventExecutorGroup eventExecutorGroup;

    public void start(int port) throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        eventExecutorGroup = new DefaultEventExecutorGroup(32);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            //aggregates an HttpMessage and its following HttpContents into a single FullHttpRequest or FullHttpResponse
                            //with no following HttpContents.
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            ch.pipeline().addLast(eventExecutorGroup, new HttpServerInboundHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            destroy();
        }
    }

    public void destroy() {
        if (eventExecutorGroup != null) {
            eventExecutorGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        log.info("Map Http Server is shut down");
    }

    public static void main(String[] args) {
        final String portString = PropertiesUtil.getInstance().GetListenPort();
        final int port = Integer.parseInt(portString);
        boolean isOK = MapDbOperation.init();
        if (isOK) {
            final MapHttpServer server = new MapHttpServer();
            try {
                log.info("Listening for connections on port " + port);
                server.start(port);
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        server.destroy();
                    }
                });
            } catch (Exception e) {
                log.error("Map Http Service failed! " + e.getMessage());
            }
        } else {
            log.error("Connect database failed!");
        }
    }

}
