package cn.gsq.host.master.handler.hook;

import cn.gsq.host.master.handler.Host;

import java.util.List;

/**
 * Project : host
 * Class : cn.gsq.host.master.handler.hook.IHostController
 *
 * @author : gsq
 * @date : 2024-10-15 16:00
 * @note : It's not technology, it's art !
 **/
public interface IHostController {

    List<Host> load();

    Host create(String hostname, String data);

    void remove(String hostname);

}
