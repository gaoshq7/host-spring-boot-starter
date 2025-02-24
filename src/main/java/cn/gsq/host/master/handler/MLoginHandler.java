package cn.gsq.host.master.handler;

import cn.gsq.host.common.protobuf.Command;
import cn.gsq.host.common.protobuf.Message;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.gsq.host.common.Event;
import cn.gsq.host.common.MsgUtil;
import cn.gsq.host.common.models.LoginDTO;
import cn.gsq.host.master.handler.hook.ILoginReceiver;
import io.netty.channel.ChannelHandlerContext;

/**
 * Project : host
 * Class : cn.gsq.host.master.handler.MLoginHandler
 *
 * @author : gsq
 * @date : 2024-09-04 16:48
 * @note : It's not technology, it's art !
 **/
public class MLoginHandler extends MAbstractHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) {
        Message.BaseMsg msg = (Message.BaseMsg) data;
        if (msg.getType() == Command.CommandType.AUTH) {
            debug(StrUtil.format("{}主机提交认证信息：{}", msg.getClientId(), msg.getData()));
            super.setClientId(ctx, msg.getClientId());
            ILoginReceiver receiver = getLoginReceiver();
            LoginDTO loginDTO = receiver.auth(msg.getClientId(), msg.getData());
            if (loginDTO.isAuth()) {
                boolean isJoin = getHostManager().join(msg.getClientId(), msg.getData(), ctx);
                if (isJoin) {
                    getMsgReceiver().online(msg.getClientId());
                } else {
                    loginDTO.setAuth(false);
                    warn(StrUtil.format("{}主机已存在，不可重复添加。", msg.getClientId()));
                }
            }
            debug(StrUtil.format("{}主机认证结果：{}", msg.getClientId(), loginDTO));
            ctx.channel().writeAndFlush(
                    MsgUtil.createMsg(msg.getClientId(), Command.CommandType.AUTH_BACK, loginDTO.toString())
            );
            if (!loginDTO.isAuth()) {
                super.auth(ctx, false);
                debug(StrUtil.format("{}主机认证失败，5秒后将断开链接...", msg.getClientId()));
                ThreadUtil.safeSleep(5000);
                ctx.channel().close();
                debug(StrUtil.format("与{}主机的链接已断开。", msg.getClientId()));
            }
        } else if (msg.getType() == Command.CommandType.RESIGN_BACK) {
            debug(StrUtil.format("{}主机已完成退役准备，即将断开链接。", getClientId(ctx)));
            super.setOfflineEvent(ctx, Event.SLAVE_DECOMMISSIONED);
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (super.isAuth(ctx)) {
            Event event = getOfflineEvent(ctx);
            getMsgReceiver().offline(getClientId(ctx), event);
            warn(StrUtil.format("与{}主机的链接断开（{}）。", getClientId(ctx), event.getContent()));
        } else {
            warn(getClientId(ctx) + "主机登录认证失败。");
        }
    }

}
