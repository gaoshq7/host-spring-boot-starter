package cn.gsq.host.config;

import cn.gsq.host.common.protobuf.Message;
import cn.gsq.host.slave.HmClient;
import cn.gsq.host.slave.handler.SHeartbeatHandler;
import cn.gsq.host.slave.handler.SLoginHandler;
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

import java.util.concurrent.TimeUnit;

/**
 * Project : host
 * Class : cn.gsq.host.config.SlaveAutoConfigure
 *
 * @author : gsq
 * @date : 2024-09-03 14:36
 * @note : It's not technology, it's art !
 **/
@Configuration
@Conditional(SlaveAutoConfigure.SCondition.class)
@EnableConfigurationProperties(SProperties.class)
public class SlaveAutoConfigure {

    @Bean(name = "host_manager_client")
    public HmClient hmClient(@Qualifier("channel_initializer") ChannelInitializer<SocketChannel> initializer, SProperties properties) {
        return new HmClient(initializer, properties.getServer(), properties.getPort());
    }

    @Bean(name = "channel_initializer")
    public ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline cp = socketChannel.pipeline();
                cp.addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
                cp.addLast(new ProtobufVarint32FrameDecoder());
                cp.addLast(new ProtobufDecoder(Message.BaseMsg.getDefaultInstance()));
                cp.addLast(new ProtobufVarint32LengthFieldPrepender());
                cp.addLast(new ProtobufEncoder());
                cp.addLast(new SHeartbeatHandler(), new SLoginHandler());
            }
        };
    }

    protected static class SCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String isEnable = context.getEnvironment().getProperty("host.hm.slave.enabled");
            return Boolean.parseBoolean(isEnable);
        }

    }

}
