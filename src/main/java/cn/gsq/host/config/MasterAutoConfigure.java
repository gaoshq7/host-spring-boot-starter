package cn.gsq.host.config;

import cn.gsq.host.common.protobuf.Message;
import cn.gsq.host.master.HmServer;
import cn.gsq.host.master.HostManager;
import cn.gsq.host.master.handler.HostManagerImpl;
import cn.gsq.host.master.handler.MHeartbeatHandler;
import cn.gsq.host.master.handler.MLoginHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Project : host
 * Class : cn.gsq.host.config.MasterAutoConfigure
 *
 * @author : gsq
 * @date : 2024-09-03 14:36
 * @note : It's not technology, it's art !
 **/
@Configuration
@Conditional(MasterAutoConfigure.MCondition.class)
@EnableConfigurationProperties(MProperties.class)
public class MasterAutoConfigure {

    @Bean(name = "host_manager")
    public HostManager hostManager() {
        return new HostManagerImpl();
    }

    @Bean(name = "host_manager_server")
    public HmServer hmServer(@Qualifier("channel_initializer") ChannelInitializer<SocketChannel> initializer, MProperties properties) {
        return new HmServer(initializer, new InetSocketAddress(properties.getIp(), properties.getPort()));
    }

    @Bean(name = "channel_initializer")
    public ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline cp = socketChannel.pipeline();
                cp.addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
                cp.addLast(new ProtobufVarint32FrameDecoder());
                cp.addLast(new ProtobufDecoder(Message.BaseMsg.getDefaultInstance()));
                cp.addLast(new ProtobufVarint32LengthFieldPrepender());
                cp.addLast(new ProtobufEncoder());
                cp.addLast(new MHeartbeatHandler(), new MLoginHandler());
            }
        };
    }

    protected static class MCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String isEnable = context.getEnvironment().getProperty("host.hm.master.enabled");
            return Boolean.parseBoolean(isEnable);
        }

    }

}
