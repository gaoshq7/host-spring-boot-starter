package cn.gsq.host.slave;

import lombok.Getter;
import lombok.Setter;

/**
 * Project : host
 * Class : cn.gsq.host.slave.LinkEnv
 *
 * @author : gsq
 * @date : 2024-10-15 10:48
 * @note : It's not technology, it's art !
 **/
@Getter
public class LinkEnv {

    @Setter
    private boolean identification;

    @Setter
    private boolean resign;

    private int counter;

    public LinkEnv() {
        this.identification = true;
    }

    public void increment() {
        this.counter++;
    }

    public void reset() {
        this.counter = 0;
    }

}
