import cqjsdk.msg.*;
import cqjsdk.server.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

class Code{
    private String code;
    private String syntax;
    private String poster;
    private static final String paste_url = "https://paste.ubuntu.com/";
    private static String last_url = "null";
    public Code(String code, String syntax, String poster) {
        this.code = code;
        this.syntax = syntax;
        this.poster = poster;
    }

    public static String getLastUrl(){
        return last_url;
    }

    public String pasteIt(){
        try {
            URL url = new URL(paste_url);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);
            OutputStream out = con.getOutputStream();
            out.write(String.format("poster=%s&syntax=%s&content=%s",
                    URLEncoder.encode(this.poster,"UTF-8"),
                    URLEncoder.encode(this.syntax,"UTF-8"),
                    URLEncoder.encode(this.code,"UTF-8")).getBytes());
            Map<String, List<String>> headers = con.getHeaderFields();
            String pasted_url = null;
            try{
                pasted_url = headers.get("Location").get(0);
                last_url = pasted_url;
            }
            catch (Exception ex){
                ex.printStackTrace();
                for (Map.Entry<String, List<String>> header : con.getHeaderFields().entrySet()) {
                    System.out.println(header.getKey() + "=" + header.getValue());
                }
                return null;
            }
            return pasted_url;
        }
        catch (Exception ex){
            return null;
        }
    }
}

class Command extends CQJModule{
    private final String Admin;
    private FuDuJi fuDuJi;
    private final String help_text;
    private final String acess_error_text;
    private final String format_error_text;
    private final String network_error_text;
    public Command(String admin){
        String[] strings = {"GroupMessage","PrivateMessage"};
        register(strings);
        this.fuDuJi = new FuDuJi();
        this.Admin = admin;
        this.help_text = "目前支持指令:\n" +
                "所有人:\n" +
                "/help 显示本帮助\n" +
                "管理员:\n" +
                "/fudu [on,off=on] [group=currentgourp] 开启或者关闭指定群付读，如果无任何参数默认打开当前群复读\n" +
                "/code [poster] [syntax=cpp,c...] [code] Ubuntu Paste。无任何参数返回上次代码链接。";
        this.acess_error_text = "权限错误";
        this.format_error_text = "格式错误";
        this.network_error_text = "网络错误，请重试";
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
                                        fuDuJi.removeGroup(args[2]);
                                    }
                                    else{
                                        fuDuJi.removeGroup(msg.getGroup());
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
                case "/help": // TODO:help [command]
                    sendMsg(new SendGroupMsg(msg.getGroup(),help_text));
                    return true;
                case "/code":
                    args = text.split(" ", 4);
                    if(args.length == 4){
                        Code cd = new Code(args[3], args[2], args[1]);
                        String url = cd.pasteIt();
                        if(url==null){
                            sendMsg(new SendGroupMsg(msg.getGroup(), network_error_text));
                        }
                        else{
                            sendMsg(new SendGroupMsg(msg.getGroup(), url));
                        }
                    }
                    else if(args.length == 1){
                        sendMsg(new SendGroupMsg(msg.getGroup(), Code.getLastUrl()));
                    }
                    else sendMsg(new SendGroupMsg(msg.getGroup(),format_error_text));
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