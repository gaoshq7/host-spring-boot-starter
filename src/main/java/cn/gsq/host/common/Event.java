package cn.gsq.host.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Project : host
 * Class : cn.gsq.host.common.Event
 *
 * @author : gsq
 * @date : 2024-10-11 17:20
 * @note : It's not technology, it's art !
 **/
@Getter
@AllArgsConstructor
public enum Event {

    SLAVE_HEARTBEAT_TIMEOUT(EventType.OFFLINE, "主机心跳超时"),

    SLAVE_SHUTDOWN(EventType.OFFLINE, "主机失联"),

    SLAVE_DECOMMISSIONED(EventType.OFFLINE, "主机退役"),

    SLAVE_CHANNEL_EXCEPTION(EventType.OFFLINE, "主机信道异常");

    private final EventType type;

    private final String content;

}
