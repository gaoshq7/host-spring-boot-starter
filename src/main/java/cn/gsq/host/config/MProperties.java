package cn.gsq.host.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Project : host
 * Class : cn.gsq.host.config.MProperties
 *
 * @author : gsq
 * @date : 2024-09-03 16:23
 * @note : It's not technology, it's art !
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "host.hm.master")
public class MProperties {

    private boolean enabled = false;

    private boolean debug = false;

    private String ip = "0.0.0.0";

    private Integer port = 19999;

}
