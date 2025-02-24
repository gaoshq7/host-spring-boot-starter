<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">主机管理</h1>
<h4 align="center">基于主从模式的集群主机管理框架，主从节点之间保持长链接。</h4>

---

## 特性

- **长链接**：基于 Netty 实现一个主节点和若干个从节点之间保持TCP长链接。
- **心跳包**：使用 protobuf 序列化方案实现心跳包内容定制化。
- **失联通知**：提供从节点失联通知接口供使用者实现相应的业务逻辑。
- **断线重连**：因其它原因导致的主从节点断开链接支持断线重连。
- **链接认证**：提供接口供使用者根据业务逻辑实现从节点加入集群的认证。

## 快速开始

### 引入依赖
```xml
<dependency>
    <groupId>io.github.gaoshq7</groupId>
    <artifactId>host-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
### 管理节点配置
```yaml
galaxy:
  heartbeat:
    server:
      enabled: true
      debug: true
```
### 管理节点启动代码
```java
package io.github.gaoshq7.master;

import cn.hutool.extra.spring.SpringUtil;
import io.github.gsq.hm.master.HmServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MasterApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(MasterApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        HmServer server = SpringUtil.getBean(HmServer.class);
        server.start(); // 此处代码阻塞
    }
}
```
### 工作节点配置
```yaml
galaxy:
  heartbeat:
    agent:
      enabled: true
      debug: true
```
### 工作节点启动代码
```java
package io.github.gaoshq7.slave;

import cn.hutool.extra.spring.SpringUtil;
import io.github.gsq.hm.slave.HmClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SlaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlaveApplication.class, args);
        HmClient client = SpringUtil.getBean(HmClient.class);
        client.run();
    }

}
```
### 管理节点控制台
![image](https://github.com/gaoshq7/cornerstone/blob/main/host-manager-spring-boot-starter/images/master-console.jpg)
### 工作节点控制台
![image](https://github.com/gaoshq7/cornerstone/blob/main/host-manager-spring-boot-starter/images/slave-console.jpg)
## 自定义互联业务逻辑
### 工作节点 - 自定义注册请求
```java
package io.github.gaoshq7.slave;

import cn.hutool.core.util.IdUtil;
import io.github.gsq.hm.slave.handler.hook.ILoginProvider;
import org.springframework.stereotype.Component;

@Component
public class SLogin implements ILoginProvider {

    @Override
    public String create() {
        String request = "创建注册请求对象，并序列化为json字符串。";
        return request;
    }

    @Override
    public void result(String data) {
        // data为管理节点反馈的注册信息序列化成的json字符串。
    }

}
```
### 管理节点 - 自定义认证逻辑
```java
package io.github.gaoshq7.master;

import cn.hutool.core.util.IdUtil;
import io.github.gsq.hm.common.models.LoginDTO;
import io.github.gsq.hm.master.handler.hook.ILoginReceiver;
import org.springframework.stereotype.Component;

@Component
public class MLogin implements ILoginReceiver {

    @Override
    public LoginDTO auth(String clientId, String data) {
        // clientId是请求注册主机的主机名
        // data是请求注册主机的注册信息对象序列化成的json字符串
        LoginDTO loginDTO = new LoginDTO();
        // ...（注册是否通过的业务逻辑）
        loginDTO.setAuth(true);  // 允许注册
        loginDTO.setData("反馈给请求注册主机的json字符串。");
        return loginDTO;
    }

}
```
### 工作节点 - 自定义心跳包
```java
package io.github.gaoshq7.slave;

import io.github.gsq.hm.slave.handler.hook.IHeartbeatProvider;
import org.springframework.stereotype.Component;

@Component
public class SHeartbeat implements IHeartbeatProvider {

    @Override
    public String create() {
        String heartbeat = "创建注心跳对象，并序列化为json字符串。";
        return heartbeat;
    }

    @Override
    public void result(String data) {
        // data为管理节点反馈的心跳对象序列化成的json字符串。
    }

}
```
### 管理节点 - 自定义心跳响应包
```java
package io.github.gaoshq7.master;

import cn.hutool.core.util.IdUtil;
import io.github.gsq.hm.master.handler.hook.IHeartbeatReceiver;
import org.springframework.stereotype.Component;

@Component
public class MHeartbeat implements IHeartbeatReceiver {

    @Override
    public String handle(String clientId, String data) {
        // clientId是请求注册主机的主机名
        // data是工作节点的心跳包对象序列化成的json字符串
        String pong = "创建心跳包回执对象，并序列化为json字符串。";
        return pong;
    }

}
```
### 工作节点 - 自定义互联业务事件逻辑
```java
package io.github.gaoshq7.slave;

import io.github.gsq.hm.slave.handler.hook.ISMsgReceiver;
import org.springframework.stereotype.Component;

@Component
public class SMsg implements ISMsgReceiver {

    @Override
    public void loseOnce() {
        // 与主机失联一次触发事件
    }

    @Override
    public void loseTwice() {
        // 与主机失联二次触发事件
    }

    @Override
    public void loseLink() {
        // 与主机断开链接触发事件
    }

}
```
### 管理节点 - 自定义互联业务事件逻辑
```java
package io.github.gaoshq7.master;

import io.github.gsq.hm.common.Event;
import io.github.gsq.hm.master.handler.hook.IMMsgReceiver;
import org.springframework.stereotype.Component;

@Component
public class MMsg implements IMMsgReceiver {

    @Override
    public void loseOnce(String clientId) {
       // 与“clientId”主机失联一次触发事件
    }

    @Override
    public void loseTwice(String clientId) {
        // 与“clientId”主机失联二次触发事件
    }

    @Override
    public void online(String clientId) {
        // “clientId”主机注册成功上线触发事件
    }

    @Override
    public void offline(String clientId, Event event) {
        // 与“clientId”主机断开链接触发事件
        // event为断开链接事件的原因，包括：
        // 1.主机心跳超时
        // 2.主机失联
        // 3.主机退役
        // 4.主机信道异常
    }

}
```
### 管理节点 - 主机管理接口
```java
package io.github.gaoshq7.master;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import io.github.gsq.hm.master.HostManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class TestController {

    @Autowired
    // CustomHost为自定义主机实现（下面步骤有说明）
    private HostManager<CustomHost> manager;
    

    // 获取所有主机及状态
    @GetMapping("/list")
    public List<Pair<String, String>> getHostnames() {
        return CollUtil.map(
                manager.list(),
                host -> new Pair<>(
                        host.getHostname(), 
                        host.isConnected() ? "链接中" : "已断开"
                ),
                true
        );
    }

    // 根据主机名获取对应的主机
    @GetMapping("/g/{hostname}")
    public CustomHost getHost(@PathVariable("hostname") String hostname) {
        return manager.get(hostname);
    }

    // 根据主机名关闭工作节点与管理节点之间的网路链接
    @GetMapping("/c/{hostname}")
    public String close(@PathVariable("hostname") String hostname) {
        manager.close(hostname);
        return "success";
    }

    // 根据主机名删除工作节点
    @GetMapping("/r/{hostname}")
    public String remove(@PathVariable("hostname") String hostname) {
        manager.remove(hostname);
        return "success";
    }

}
```
### 管理节点 - 自定义主机实例
```java
package io.github.gaoshq7.master;

import io.github.gsq.hm.master.handler.Host;
import lombok.Getter;
import lombok.Setter;

// 自定义主机实现必须继承“Host”
@Getter
@Setter
public class CustomHost extends Host {

    private String customInfo;

    public CustomHost(String hostname, String customInfo) {
        super(hostname);
        this.customInfo = customInfo;
    }

    // 覆盖此函数每次链接重建的时候会重新加载自定义的属性
    // 根据业务需要选择覆盖或不覆盖
    @Override
    protected void update(String data) {
        this.customInfo = data;
    }

}
```
### 管理节点 - 工作节点持久化业务逻辑
```java
package io.github.gaoshq7.master;

import cn.hutool.core.collection.CollUtil;
import io.github.gsq.hm.master.handler.Host;
import io.github.gsq.hm.master.handler.hook.IHostController;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HostEngine implements IHostController {

    // 程序启动时加载的主机集合
    // 注意：集合中的实例对象必须与使用“HostManager<CustomHost>”
    //      时的泛型相同
    @Override
    public List<Host> load() {
        return CollUtil.newArrayList(
                new CustomHost("Dream", "大西瓜"),
                new CustomHost("host09", "小番茄"),
                new CustomHost("host07", "小趴菜")
        );
    }

    // 工作节点成功链接到管理节点时会调用该函数
    @Override
    public Host create(String hostname, String data) {
        return new CustomHost(hostname, data);
    }

    // 主机退役时会调用该函数
    @Override
    public void remove(String hostname) {
        // 删除“hostname”相关业务逻辑
    }

}
```
> [**注意**]
> 这些接口可以根据业务需要自由选择进行实现，并加入到bean环境中，无需全部实现。