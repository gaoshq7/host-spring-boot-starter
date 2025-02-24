package cn.gsq.host.master.handler.hook;

import cn.gsq.host.common.models.LoginDTO;

/**
 * Project : host
 * Class : cn.gsq.host.master.handler.hook.ILoginReceiver
 *
 * @author : gsq
 * @date : 2024-09-29 15:47
 * @note : It's not technology, it's art !
 **/
public interface ILoginReceiver {

    LoginDTO auth(String clientId, String data);

}
