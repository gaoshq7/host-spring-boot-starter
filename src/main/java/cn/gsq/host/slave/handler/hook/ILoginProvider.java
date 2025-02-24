package cn.gsq.host.slave.handler.hook;

/**
 * Project : host
 * Class : cn.gsq.host.slave.handler.hook.ILoginProvider
 *
 * @author : gsq
 * @date : 2024-09-29 15:38
 * @note : It's not technology, it's art !
 **/
public interface ILoginProvider {

    String create();

    void result(String data);

}
