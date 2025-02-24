package cn.gsq.host.slave.handler;

import cn.gsq.host.Constant;
import cn.gsq.host.common.MsgUtil;
import cn.gsq.host.common.protobuf.Command;
import cn.gsq.host.common.protobuf.Message;
import cn.gsq.host.slave.handler.hook.IHeartbeatProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * Project : host
 * Class : cn.gsq.host.slave.handler.SHeartbeatHandler
 *
 * @author : gsq
 * @date : 2024-09-04 17:00
 * @note : It's not technology, it's art !
 **/
public class SHeartbeatHandler extends SAbstractHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (getCount(ctx) < Constant.MAX_LOSE_TIME) {
                IHeartbeatProvider provider = getHeartbeatProvider();
                String heartbeat = provider.create();
                ctx.writeAndFlush(MsgUtil.createMsg(Constant.HOSTNAME, Command.CommandType.PING, heartbeat));
                note(ctx);
                debug("发送心跳包：" + heartbeat);
            } else {
                ctx.channel().close();
                warn("心跳包丢失三次，已断开链接。");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.BaseMsg msg) {
        if (msg.getType() == Command.CommandType.PONG) {
            super.reset(ctx);
            IHeartbeatProvider provider = getHeartbeatProvider();
            provider.result(msg.getData());
            debug("收到心跳响应：" + msg.getData());
        } else {
            if(ctx.channel().isOpen()) {
                ctx.fireChannelRead(msg);
            }
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        super.logger.error("主机信道发生异常：", cause);
        getClient().stop();
        debug("心跳客户端已关闭。");
    }

    private void note(ChannelHandlerContext ctx) {
        switch (getCount(ctx)) {
            case 1 :
                debug("心跳包丢失一次...");
                getMsgReceiver().loseOnce();
                break;
            case 2 :
                debug("心跳包丢失二次...");
                getMsgReceiver().loseTwice();
                break;
        }
        super.record(ctx);
    }

}
