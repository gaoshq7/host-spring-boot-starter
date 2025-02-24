package cn.gsq.host.config;

import cn.gsq.host.Constant;
import cn.gsq.host.master.handler.HostManagerImpl;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Project : host
 * Class : cn.gsq.host.config.InitializeListener
 *
 * @author : gsq
 * @date : 2024-09-14 11:28
 * @note : It's not technology, it's art !
 **/
public class InitializeListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        boolean onMaster = Boolean.parseBoolean(context.getEnvironment().getProperty("host.hm.master.enabled"));
        boolean onSlave = Boolean.parseBoolean(context.getEnvironment().getProperty("host.hm.slave.enabled"));
        if (onMaster) {
            boolean masterIsDebug = Boolean.parseBoolean(context.getEnvironment().getProperty("host.hm.master.debug"));
            initLogSystem(Constant.MASTER, masterIsDebug ? "debug" : "info");
            mInitialize(context);
        }
        if (onSlave) {
            boolean slaveIsDebug = Boolean.parseBoolean(context.getEnvironment().getProperty("host.hm.slave.debug"));
            initLogSystem(Constant.SLAVE, slaveIsDebug ? "debug" : "info");
            sInitialize(context);
        }
    }

    private void initLogSystem(String role, String level) {
        LoggingSystem system = LoggingSystem.get(LoggingSystem.class.getClassLoader());
        system.setLogLevel("io.netty", LogLevel.ERROR);
        system.setLogLevel("cn.gsq.host." + role, LogLevel.valueOf(level.trim().toUpperCase()));
    }

    private void mInitialize(ApplicationContext context) {
        HostManagerImpl manager = context.getBean(HostManagerImpl.class);
        manager.load();
    }

    private void sInitialize(ApplicationContext context) {

    }

}
