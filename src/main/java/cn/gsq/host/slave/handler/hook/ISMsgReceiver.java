package cn.gsq.host.slave.handler.hook;

/**
 * Project : host
 * Class : cn.gsq.host.slave.handler.hook.ISMsgReceiver
 *
 * @author : gsq
 * @date : 2024-09-29 15:44
 * @note : It's not technology, it's art !
 **/
public interface ISMsgReceiver {

    void loseOnce();

    void loseTwice();

    void loseLink();

}
