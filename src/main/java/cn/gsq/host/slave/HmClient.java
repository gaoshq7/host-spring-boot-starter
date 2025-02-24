package cn.gsq.host.slave;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Project : host
 * Class : cn.gsq.host.slave.HmClient
 *
 * @author : gsq
 * @date : 2024-09-03 17:13
 * @note : It's not technology, it's art !
 **/
@Slf4j
public class HmClient {

    private final String ip;

    private final Integer port;

    private final ChannelInitializer<SocketChannel> initializer;

    private EventLoopGroup group;

    public HmClient(ChannelInitializer<SocketChannel> initializer, String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        this.initializer = initializer;
    }

    public void run() {
        stop();
        this.group = new NioEventLoopGroup();
        loopConnect(this.group);
    }

    public boolean isShutdown() {
        return group == null || group.isShutdown();
    }

    public void stop() {
        if (this.group != null) {
            this.group.shutdownGracefully();
            this.group = null;
        }
    }

    public void reconnect(EventLoopGroup group) {
        loopConnect(group);
    }

    private void loopConnect(EventLoopGroup group) {
        group.execute(() -> {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(this.initializer);
            bootstrap.remoteAddress(this.ip, this.port);
            GenericFutureListener<ChannelFuture> listener = cf -> {
                final EventLoop eventLoop = cf.channel().eventLoop();
                if (!cf.isSuccess()) {
                    log.warn("连接服务器 {}:{} 失败，5s后重新尝试连接！", this.ip, this.port);
                    eventLoop.schedule(() -> loopConnect(eventLoop), 5, TimeUnit.SECONDS);
                }
            };
            bootstrap.connect().addListener(listener);
        });
    }

}
