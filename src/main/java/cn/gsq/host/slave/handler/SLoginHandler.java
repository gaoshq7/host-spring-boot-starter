package cn.gsq.host.slave.handler;

import cn.gsq.host.common.protobuf.Command;
import cn.gsq.host.common.protobuf.Message;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.gsq.host.Constant;
import cn.gsq.host.common.MsgUtil;
import cn.gsq.host.common.models.LoginDTO;
import cn.gsq.host.slave.HmClient;
import cn.gsq.host.slave.handler.hook.ILoginProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

/**
 * Project : host
 * Class : cn.gsq.host.slave.handler.SLoginHandler
 *
 * @author : gsq
 * @date : 2024-09-04 16:56
 * @note : It's not technology, it's art !
 **/
public class SLoginHandler extends SAbstractHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        super.initialize(ctx);
        ILoginProvider provider = getLoginProvider();
        String data = provider.create();
        debug(StrUtil.format("{}主机创建登录信息：{}", Constant.HOSTNAME, data));
        ctx.writeAndFlush(
                MsgUtil.createMsg(
                        Constant.HOSTNAME,
                        Command.CommandType.AUTH,
                        data
                )
        );
        debug("登录信息提交成功。");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.BaseMsg msg) {
        if (msg.getType() == Command.CommandType.AUTH_BACK) {
            debug(StrUtil.format("{}主机收到登录结果：{}", Constant.HOSTNAME, msg.getData()));
            LoginDTO loginDTO = JSONUtil.toBean(msg.getData(), LoginDTO.class);
            ILoginProvider provider = getLoginProvider();
            provider.result(loginDTO.getData());
            if (loginDTO.isAuth()) {
                debug(StrUtil.format("{}主机登录成功。", Constant.HOSTNAME));
            } else {
                super.authFailure(ctx);
                warn(StrUtil.format("{}主机登录失败，将不会启动断线重连。", Constant.HOSTNAME));
            }
        } else if (msg.getType() == Command.CommandType.RESIGN) {
            super.resign(ctx);
            debug(StrUtil.format("{}主机收到退役指令，等待退役。", Constant.HOSTNAME));
            ctx.writeAndFlush(
                    MsgUtil.createMsg(
                            Constant.HOSTNAME,
                            Command.CommandType.RESIGN_BACK,
                            ""
                    )
            );
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        debug(StrUtil.format("{}主机与master之间的链接断开。", Constant.HOSTNAME));
        getMsgReceiver().loseLink();
        super.channelInactive(ctx);
        HmClient client = getClient();
        if (super.isAuthenticated(ctx) && !client.isShutdown() && !super.isResigned(ctx)) {
            debug(StrUtil.format("{}主机5秒后启动断线重连机制。", Constant.HOSTNAME));
            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(() -> client.reconnect(eventLoop), 5L, TimeUnit.SECONDS);
        }
    }

}
