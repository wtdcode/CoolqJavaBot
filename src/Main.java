import cqjsdk.server.*;

public class Main {
    public static void go(Config config){
        Server c = Server.getServer(config.getTarget_port(),config.getServer_port());
        Command cmd = new Command(config, c);
        c.start();
        Logger.Log("服务器启动");
    }
    public static void main(String[] args){
        Config config = Config.load("config.json");
        go(config);
    }
}