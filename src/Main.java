import cqjsdk.msg.*;
import cqjsdk.*;
import cqjsdk.server.Server;

public class Main {
    public class FuDuJi extends CQJModule {
        public FuDuJi(){
            String[] strings ={"GroupMessage"};
            register(strings); // 这里注册监听的信息种类
        }
        protected void dealGroupMsg(RecvGroupMsg msg){ // 有新的群聊消息的时候这个成员函数会被调用
            String text = msg.getText();
            text = "（".concat(text).concat("）");
            SendGroupMsg smsg = new SendGroupMsg(msg.getGroup(), text);
            sendMsg(smsg);
        }
    }
    public void go(){
        FuDuJi f = new FuDuJi(); // 实例化Module
        Server c = Server.getServer(11235,23333); // 创建客户端
        c.start();
    }
    public static void main(String[] args){
        Main fdj = new Main();
        fdj.go();
    }
}