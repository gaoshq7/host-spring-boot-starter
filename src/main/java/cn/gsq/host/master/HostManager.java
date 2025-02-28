package cn.gsq.host.master;

import cn.gsq.host.master.handler.Host;

import java.util.List;
import java.util.Set;

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

    Set<String> getHostnames();

    List<T> getActives();

    List<T> getDeads();

    boolean isExist(String hostname);

    boolean isActive(String hostname);

}
