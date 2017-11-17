import com.sun.org.apache.xpath.internal.operations.Bool;
import cqjsdk.msg.*;
import cqjsdk.server.*;

class FuDuJi extends CQJModule {
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

class WTD extends CQJModule{

}

class Command extends CQJModule{
    private final String Admin;
    private FuDuJi fuDuJi;
    public Command(String admin){
        String[] strings = {"GroupMessage","PrivateMessage"};
        register(strings);
        fuDuJi = new FuDuJi();
        this.Admin = admin;
    }
    protected void dealGroupMsg(RecvGroupMsg msg){
        if(msg.getQq().equals(this.Admin)){
            String text = getPlainText(msg.getText());
            if(text.indexOf(0) == '/'){
                String[] args = text.split(" ");
                switch (args[0]){
                    case "/fudu":
                        if(args.length > 1){
                            switch (args[1]) {
                                case "on":
                                    fuDuJi.run();
                                    break;
                                case "off":
                                    fuDuJi.stop();
                                    break;
                                default:
                                    fuDuJi.switchstat();
                                    break;
                            }
                        }
                        else fuDuJi.switchstat();
                }
            }
        }
    }
}

public class Main {

    public void go(){
        Command cmd = new Command("771644612");
        Server c = Server.getServer(11235,23333); // 创建客户端
        c.start();
    }

    public static void main(String[] args){
        Main fdj = new Main();
        fdj.go();
    }
}