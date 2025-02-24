package cn.gsq.host.master.handler;

import cn.hutool.extra.spring.SpringUtil;
import cn.gsq.host.common.EnvUtil;
import cn.gsq.host.common.Event;
import cn.gsq.host.common.models.LoginDTO;
import cn.gsq.host.master.ChannelContext;
import cn.gsq.host.master.handler.hook.IHeartbeatReceiver;
import cn.gsq.host.master.handler.hook.ILoginReceiver;
import cn.gsq.host.master.handler.hook.IMMsgReceiver;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Project : host
 * Class : cn.gsq.host.master.handler.MAbstractHandler
 *
 * @author : gsq
 * @date : 2024-09-03 17:08
 * @note : It's not technology, it's art !
 **/
@ChannelHandler.Sharable
public abstract class MAbstractHandler extends ChannelInboundHandlerAdapter {

    private HostManagerImpl<Host> manager;

    private ILoginReceiver loginReceiver;

    private IHeartbeatReceiver heartbeatReceiver;

    private IMMsgReceiver immMsgReceiver;

    protected final InternalLogger logger;

    private final AttributeKey<ChannelContext> context = AttributeKey.valueOf("context");

    protected MAbstractHandler() {
        this.logger = InternalLoggerFactory.getInstance(this.getClass());
    }

    protected final void auth(ChannelHandlerContext ctx, boolean auth) {
        ctx.channel().attr(this.context).get().setAuth(auth);
    }

    protected final boolean isAuth(ChannelHandlerContext ctx) {
        return ctx.channel().attr(this.context).get().isAuth();
    }

    protected final void setClientId(ChannelHandlerContext ctx, String clientId) {
        ctx.channel().attr(this.context).set(new ChannelContext(clientId));
    }

    protected final String getClientId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(this.context).get().getClientId();
    }

    protected final int getCount(ChannelHandlerContext ctx) {
        return ctx.channel().attr(this.context).get().getCounter();
    }

    protected final void record(ChannelHandlerContext ctx) {
        ctx.channel().attr(this.context).get().increment();
    }

    protected final void reset(ChannelHandlerContext ctx) {
        ctx.channel().attr(this.context).get().reset();
    }

    protected final void setOfflineEvent(ChannelHandlerContext ctx, Event event) {
        ctx.channel().attr(this.context).get().setOfflineEvent(event);
    }

    protected final Event getOfflineEvent(ChannelHandlerContext ctx) {
        return ctx.channel().attr(this.context).get().getOfflineEvent();
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

    protected final ILoginReceiver getLoginReceiver() {
        if (this.loginReceiver == null) {
            ILoginReceiver receiver = EnvUtil.getBean(ILoginReceiver.class);
            this.loginReceiver = receiver != null ? receiver : (clientId, data) -> new LoginDTO(true, "");
        }
        return this.loginReceiver;
    }

    protected final IHeartbeatReceiver getHeartbeatReceiver() {
        if (this.heartbeatReceiver == null) {
            IHeartbeatReceiver receiver = EnvUtil.getBean(IHeartbeatReceiver.class);
            this.heartbeatReceiver = receiver != null ? receiver : (clientId, data) -> "";
        }
        return this.heartbeatReceiver;
    }

    protected final IMMsgReceiver getMsgReceiver() {
        if (this.immMsgReceiver == null) {
            IMMsgReceiver receiver = EnvUtil.getBean(IMMsgReceiver.class);
            this.immMsgReceiver = receiver != null ? receiver :
                    new IMMsgReceiver() {

                        @Override
                        public void loseOnce(String clientId) {

                        }

                        @Override
                        public void loseTwice(String clientId) {

                        }

                        @Override
                        public void online(String clientId) {

                        }

                        @Override
                        public void offline(String clientId, Event event) {

                        }

                    };
        }
        return this.immMsgReceiver;
    }

    protected HostManagerImpl<Host> getHostManager() {
        if (this.manager == null) {
            this.manager = SpringUtil.getBean(HostManagerImpl.class);
        }
        return this.manager;
    }

}
