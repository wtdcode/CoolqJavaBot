# CoolqJavaBot

## 简介

一个基于[CoolqSocketAPI](https://github.com/yukixz/cqsocketapi)二次开发的CoolqJavaBot。

感谢@[jqqqqqqqqqq](https://github.com/jqqqqqqqqqq)给与了非常多的关键性指导（以及他的[repo](https://github.com/jqqqqqqqqqq/coolq-telegram-bot)）。

因为Coolq没有现成的Java轮子，只能自己造了。

## 最近

- 把Sender和Dispatcher全部放入Server，预计以后结构不会有太大变化了。
- 过滤图片、表情、at信息完成。
- repo改名，跟sdk还差得远呢（。

## TODO

- **为所有的`ex.printStackTrace()`加上应有的异常处理。**
- 把协议中剩余的Msg全部实现，并添加相应的deal接口。
- 测试框架的稳定性。
- 按照群组分发信息，构造一个GroupManager?


## 配置环境

其实Coolq部分主要是用CoolqSocketAPI转发消息，所以直接去找别人编译好的就好了（逃

[在这里](https://github.com/jqqqqqqqqqq/coolq-telegram-bot/releases)下载最新的CoolqSocketAPI后放到Coolq的插件目录，在Coolq插件里启动即可。

## 一个简单的复读机

```java
import cqjsdk.msg.*;
import cqjsdk.server.*;

public class Main {
    class FuDuJi extends CQJModule {
        public FuDuJi(){
            String[] strings ={"GroupMessage"};
            register(strings); // 这里注册监听的信息种类
        }
        protected Msg dealGroupMsg(RecvGroupMsg msg){
            String text = msg.getText();
            text = "（".concat(text).concat("）"); // 构造付读字符串
            SendGroupMsg smsg = new SendGroupMsg(msg.getGroup(), text);
            return Msg.SendandNext(smsg); // 表示发送并且不截断
        }
    }
    public void go(){
        FuDuJi f = new FuDuJi(); // 实例化Module
        Server c = Server.getServer(11235,23333); // 创建服务端
        c.start();
    }
    public static void main(String[] args){
        Main fdj = new Main();
        fdj.go();
    }
}
```

