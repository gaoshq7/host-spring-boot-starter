package cn.gsq.host.master;

import cn.gsq.host.master.handler.Host;

import java.util.List;

/**
 * Project : host
 * Class : cn.gsq.host.master.HostManager
 *
 * @author : gsq
 * @date : 2024-09-09 17:07
 * @note : It's not technology, it's art !
 **/
public interface HostManager<T extends Host> {

    List<T> list();

    T get(String hostname);

    void close(String hostname);

    void remove(String hostname);

}
