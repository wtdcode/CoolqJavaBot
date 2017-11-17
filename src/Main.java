import cqjsdk.msg.*;
import cqjsdk.server.*;

import java.util.HashSet;

class FuDuJi extends CQJModule {
    private HashSet<String> groups;
    public FuDuJi(){
        this.groups = new HashSet<String>();
        String[] strings ={"GroupMessage"};
        register(strings); // 这里注册监听的信息种类
    }

    public void addGroup(String group){
        groups.add(group);
    }

    public void removeGroup(String group){
        groups.remove(group);
    }

    protected Boolean dealGroupMsg(RecvGroupMsg msg){ // 有新的群聊消息的时候这个成员函数会被调用
        if(groups.contains(msg.getGroup())) {
            String text = msg.getText();
            text = "（".concat(text).concat("）");
            SendGroupMsg smsg = new SendGroupMsg(msg.getGroup(), text);
            sendMsg(smsg);
        }
        return false;
    }
}

class WTD extends CQJModule{

}

class Command extends CQJModule{
    private final String Admin;
    private FuDuJi fuDuJi;
    private final String help_text;
    private final String acess_error_text;
    private final String format_error_text;

    public Command(String admin){
        String[] strings = {"GroupMessage","PrivateMessage"};
        register(strings);
        this.fuDuJi = new FuDuJi();
        this.Admin = admin;
        this.help_text = "目前支持指令:\n" +
                "所有人:\n" +
                "/help 显示本帮助\n" +
                "管理员:\n" +
                "/fudu [on,off=on] [group=currentgourp]开启或者关闭指定群付读，如果无任何参数默认打开当前群复读";
        this.acess_error_text = "权限错误";
        this.format_error_text = "格式错误";
    }

    protected Boolean dealGroupMsg(RecvGroupMsg msg){
            String text = getPlainText(msg.getText());
            String[] args = text.split(" ");
            if(args.length == 0)
                return false;
            switch (args[0]){
                case "/fudu":
                    if(msg.getQq().equals(this.Admin)) {
                        if (args.length == 1) {
                            fuDuJi.addGroup(msg.getGroup());
                            fuDuJi.run();
                        }
                        else if(args.length == 2 || args.length == 3){
                            switch (args[1]) {
                                case "on":
                                    if(args.length == 3) {
                                        fuDuJi.addGroup(args[2]);
                                    }
                                    else{
                                        fuDuJi.addGroup(msg.getGroup());
                                    }
                                    break;
                                case "off":
                                    if(args.length == 3) {
                                        fuDuJi.addGroup(args[2]);
                                    }
                                    else{
                                        fuDuJi.addGroup(msg.getGroup());
                                    }
                                    break;
                                default:
                                    sendMsg(new SendGroupMsg(msg.getGroup(),format_error_text));
                                    break;
                            }
                        }
                        else sendMsg(new SendGroupMsg(msg.getGroup(),format_error_text));
                    }
                    else sendMsg(new SendGroupMsg(msg.getGroup(),acess_error_text));
                    return true;
                case "/help":
                    sendMsg(new SendGroupMsg(msg.getGroup(),help_text));
                    return true;
                default:
                    return false;
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