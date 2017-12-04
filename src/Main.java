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
        Server c = Server.getServer(11235,23333); // 创建客户端
        c.start();
    }
    public static void main(String[] args){
        Main fdj = new Main();
        fdj.go();
    }
}