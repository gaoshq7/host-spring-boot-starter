package cn.gsq.host.master.handler.hook;

import cn.gsq.host.common.Event;

/**
 * Project : host
 * Class : cn.gsq.host.master.handler.hook.IMMsgReceiver
 *
 * @author : gsq
 * @date : 2024-09-29 15:49
 * @note : It's not technology, it's art !
 **/
public interface IMMsgReceiver {

    void loseOnce(String clientId);

    void loseTwice(String clientId);

    void online(String clientId);

    void offline(String clientId, Event event);

}
