package cn.gsq.host.slave.handler;

import cn.gsq.host.common.protobuf.Message;
import cn.hutool.extra.spring.SpringUtil;
import cn.gsq.host.common.EnvUtil;
import cn.gsq.host.slave.HmClient;
import cn.gsq.host.slave.LinkEnv;
import cn.gsq.host.slave.handler.hook.IHeartbeatProvider;
import cn.gsq.host.slave.handler.hook.ILoginProvider;
import cn.gsq.host.slave.handler.hook.ISMsgReceiver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Project : host
 * Class : cn.gsq.host.slave.handler.SAbstractHandler
 *
 * @author : gsq
 * @date : 2024-09-03 17:09
 * @note : It's not technology, it's art !
 **/
public abstract class SAbstractHandler extends SimpleChannelInboundHandler<Message.BaseMsg> {

    private ILoginProvider loginProvider;

    private IHeartbeatProvider heartbeatProvider;

    private ISMsgReceiver msgReceiver;

    private HmClient client;

    private final AttributeKey<LinkEnv> env = AttributeKey.valueOf("env");

    protected final InternalLogger logger;

    protected SAbstractHandler() {
        this.logger = InternalLoggerFactory.getInstance(this.getClass());
    }

    protected final void initialize(ChannelHandlerContext ctx) {
        ctx.channel().attr(this.env).set(new LinkEnv());
    }

    protected final void authFailure(ChannelHandlerContext ctx) {
        ctx.channel().attr(this.env).get().setIdentification(false);
    }

    protected final boolean isAuthenticated(ChannelHandlerContext ctx) {
        return ctx.channel().attr(this.env).get().isIdentification();
    }

    protected final int getCount(ChannelHandlerContext ctx) {
        return ctx.channel().attr(this.env).get().getCounter();
    }

    protected final void record(ChannelHandlerContext ctx) {
        ctx.channel().attr(this.env).get().increment();
    }

    protected final void reset(ChannelHandlerContext ctx) {
        ctx.channel().attr(this.env).get().reset();
    }

    protected final void resign(ChannelHandlerContext ctx) {
        ctx.channel().attr(this.env).get().setResign(true);
    }

    protected final boolean isResigned(ChannelHandlerContext ctx) {
        return ctx.channel().attr(this.env).get().isResign();
    }

    protected final void debug(String msg) {
        if (this.logger.isDebugEnabled()) {
            this.logger.log(InternalLogLevel.DEBUG, msg);
        }
    }

    protected final void info(String msg) {
        if (this.logger.isInfoEnabled()) {
            this.logger.log(InternalLogLevel.INFO, msg);
        }
    }

    protected final void warn(String msg) {
        if (this.logger.isWarnEnabled()) {
            this.logger.log(InternalLogLevel.WARN, msg);
        }
    }

    protected final void error(String msg) {
        if (this.logger.isErrorEnabled()) {
            this.logger.log(InternalLogLevel.ERROR, msg);
        }
    }

    protected final ILoginProvider getLoginProvider() {
        if (this.loginProvider == null) {
            ILoginProvider provider = EnvUtil.getBean(ILoginProvider.class);
            this.loginProvider = provider != null ? provider :
                    new ILoginProvider() {

                        @Override
                        public String create() {
                            return "";
                        }

                        @Override
                        public void result(String data) {

                        }

                    };
        }
        return this.loginProvider;
    }

    protected final IHeartbeatProvider getHeartbeatProvider() {
        if (this.heartbeatProvider == null) {
            IHeartbeatProvider provider = EnvUtil.getBean(IHeartbeatProvider.class);
            this.heartbeatProvider = provider != null ? provider :
                    new IHeartbeatProvider() {

                        @Override
                        public String create() {
                            return "";
                        }

                        @Override
                        public void result(String data) {

                        }

                    };
        }
        return this.heartbeatProvider;
    }

    protected final ISMsgReceiver getMsgReceiver() {
        if (this.msgReceiver == null) {
            ISMsgReceiver receiver = EnvUtil.getBean(ISMsgReceiver.class);
            this.msgReceiver = receiver != null ? receiver :
                    new ISMsgReceiver() {

                        @Override
                        public void loseOnce() {

                        }

                        @Override
                        public void loseTwice() {

                        }

                        @Override
                        public void loseLink() {

                        }

                    };
        }
        return this.msgReceiver;
    }

    protected final HmClient getClient() {
        if (this.client == null) {
            this.client = SpringUtil.getBean(HmClient.class);
        }
        return this.client;
    }

}
