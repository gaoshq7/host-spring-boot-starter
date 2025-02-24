package cn.gsq.host.master.handler;

import cn.gsq.host.common.protobuf.Command;
import cn.gsq.host.common.protobuf.Message;
import cn.hutool.core.util.StrUtil;
import cn.gsq.host.Constant;
import cn.gsq.host.common.Event;
import cn.gsq.host.common.MsgUtil;
import cn.gsq.host.master.handler.hook.IHeartbeatReceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * Project : host
 * Class : cn.gsq.host.master.handler.MHeartbeatHandler
 *
 * @author : gsq
 * @date : 2024-09-04 16:53
 * @note : It's not technology, it's art !
 **/
public class MHeartbeatHandler extends MAbstractHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (getCount(ctx) >= Constant.MAX_LOSE_TIME) {
                super.setOfflineEvent(ctx, Event.SLAVE_HEARTBEAT_TIMEOUT);
                ctx.channel().close();
            } else {
                this.note(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) {
        Message.BaseMsg msg = (Message.BaseMsg) data;
        Channel channel = ctx.channel();
        if (msg.getType() == Command.CommandType.PING) {
            super.reset(ctx);
            IHeartbeatReceiver receiver = getHeartbeatReceiver();
            String result = receiver.handle(msg.getClientId(), msg.getData());
            debug(StrUtil.format("收到来自{}主机的心跳信息：{}", getClientId(ctx), msg.getData()));
            ctx.channel().writeAndFlush(
                    MsgUtil.createMsg(getClientId(ctx), Command.CommandType.PONG, result)
            );
            debug(StrUtil.format("向{}主机发送心跳回执信息：{}", getClientId(ctx), result));
        } else {
            if (channel.isOpen()) {
                ctx.fireChannelRead(msg);
            }
        }
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String clientId = getClientId(ctx);
        super.logger.error("与" + clientId + "主机信道发生异常：", cause);
        super.setOfflineEvent(ctx, Event.SLAVE_CHANNEL_EXCEPTION);
        ctx.channel().close();
        debug(StrUtil.format("与{}主机之间的信道已关闭。", clientId));
    }

    private void note(ChannelHandlerContext ctx) {
        String clientId = super.getClientId(ctx);
        switch (getCount(ctx)) {
            case 1 :
                debug(clientId + "主机心跳包丢失一次...");
                getMsgReceiver().loseOnce(clientId);
                break;
            case 2 :
                debug(clientId + "主机心跳包丢失二次...");
                getMsgReceiver().loseTwice(clientId);
                break;
        }
        super.record(ctx);
    }

}
