package cn.gsq.host.master.handler.hook;

/**
 * Project : host
 * Class : cn.gsq.host.master.handler.hook.IHeartbeatReceiver
 *
 * @author : gsq
 * @date : 2024-09-29 15:46
 * @note : It's not technology, it's art !
 **/
public interface IHeartbeatReceiver {

    String handle(String clientId, String data);

}
