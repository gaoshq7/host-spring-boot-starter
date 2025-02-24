package cn.gsq.host.common;

import cn.gsq.host.common.protobuf.Command;
import cn.gsq.host.common.protobuf.Message;
import com.google.protobuf.Timestamp;

/**
 * Project : host
 * Class : cn.gsq.host.common.MsgUtil
 *
 * @author : gsq
 * @date : 2024-09-10 10:40
 * @note : It's not technology, it's art !
 **/
public final class MsgUtil {

    public static Message.BaseMsg createMsg(String clientId, Command.CommandType type, String data) {
        Message.BaseMsg.Builder builder = Message.BaseMsg.newBuilder();
        return builder
                .setClientId(clientId)
                .setType(type)
                .setData(data)
                .setTime(Timestamp.newBuilder())
                .build();
    }

}
