package cn.gsq.host.slave.handler.hook;

/**
 * Project : host
 * Class : cn.gsq.host.slave.handler.hook.IHeartbeatProvider
 *
 * @author : gsq
 * @date : 2024-09-29 15:44
 * @note : It's not technology, it's art !
 **/
public interface IHeartbeatProvider {

    String create();

    void result(String data);

}
