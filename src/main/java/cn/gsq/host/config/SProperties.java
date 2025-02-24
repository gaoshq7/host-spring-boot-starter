package cn.gsq.host.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Project : host
 * Class : cn.gsq.host.config.SProperties
 *
 * @author : gsq
 * @date : 2024-09-03 16:23
 * @note : It's not technology, it's art !
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "galaxy.heartbeat.agent")
public class SProperties {

    private boolean enabled = false;

    private boolean debug = false;

    private String serverAddress = "127.0.0.1";

    private Integer serverPort = 19999;

}
