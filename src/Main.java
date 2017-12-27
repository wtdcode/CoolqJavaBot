import cqjsdk.server.*;

public class Main {
    public static void go(Config config){
        Server c = Server.getServer(config.getTarget_port(),config.getServer_port()); // 实例化一个服务器
        Command cmd = new Command(config, c); // 实例化CMD模块（核心模块）
        c.start(); // 服务器启动
        Logger.Log("服务器启动");
    }
    public static void main(String[] args){
        Config config = Config.load("config.json"); // 加载配置文件
        go(config);
    }
}