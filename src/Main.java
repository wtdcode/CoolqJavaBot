import cqjsdk.msg.*;
import cqjsdk.server.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class FuDuJi extends CQJModule {

    public FuDuJi(){
        String[] strings ={"GroupMessage"};
        register(strings);
    }

    protected Msg dealGroupMsg(RecvGroupMsg msg){
        String text = msg.getText();
        text = "（".concat(text).concat("）");
        SendGroupMsg smsg = new SendGroupMsg(msg.getGroup(), text);
        return Msg.SendandNext(smsg);
    }
}

class WTD extends CQJModule{
    private PreparedStatement is_wtd;
    //private PreparedStatement to_wtd;
    private PreparedStatement new_wt;
    //private PreparedStatement make_emoji;
    private PreparedStatement get_latest;
    private PreparedStatement group_get_latest;
    public WTD(String driver, String url, String username, String password){
        Connection conn = connectToDB(driver, url, username, password);
        try {
            if (conn != null && !conn.isClosed()) {
                String[] strings = {"GroupMessage"};
                register(strings);
                is_wtd = conn.prepareStatement("SELECT * FROM img.record WHERE `group`= ?  AND `md5` = ?  ", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                //to_wtd = conn.prepareStatement("UPDATE ? SET times = times + 1 WHERE md5 = ?");
                new_wt = conn.prepareStatement("INSERT INTO img.record (qq, `group`, times, emoji, md5) VALUES (?, ?, 1, 0, ?,?)");
                //make_emoji = conn.prepareStatement("UPDATE ? SET eomji = 1 WHERE md5 = ?");
                get_latest = conn.prepareStatement("SELECT * FROM img.record WHERE `group` = ? AND `qq` = ?   ORDER BY id DESC LIMIT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                group_get_latest = conn.prepareStatement("SELECT * FROM img.record WHERE `group` = ?  ORDER BY id DESC LIMIT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private Connection connectToDB(String driver, String url, String username, String password){
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url,username,password);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<Integer> water(String group, String qq, String[] md5s){
        ArrayList<Integer> wtd_times = new ArrayList<Integer>();
        for(String md5: md5s){
            try {
                is_wtd.setString(1, group);
                is_wtd.setString(2, md5);
                ResultSet rs = is_wtd.executeQuery();
                if(!rs.next()){
                    rs.moveToInsertRow();
                    rs.updateNull(1);
                    rs.updateString(2, qq);
                    rs.updateString(3, group);
                    rs.updateInt(4, 1);
                    rs.updateInt(5,0);
                    rs.updateString(6, md5);
                    rs.insertRow();
                    rs.moveToCurrentRow();
                    wtd_times.add(0);
                }
                else{
                    String origin_qq = rs.getString(2);
                    Integer emoji = rs.getInt(5);
                    if(qq.equals(origin_qq)){
                        wtd_times.add(-1);
                    }
                    else if(emoji == 1){
                        wtd_times.add(0);
                    }
                    else{
                        Integer times = rs.getInt(4);
                        rs.updateInt(4,  times + 1);
                        rs.updateRow();
                        wtd_times.add(times);
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();;
            }
        }
        return wtd_times;
    }

    public void tagEmoji(String group,String qq){
        try {
            get_latest.setString(1,group);
            get_latest.setString(2,qq);
            ResultSet rs =get_latest.executeQuery();
            if(rs.next()){
                rs.updateInt(5, 1);
                rs.updateRow();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void tagEmoji(String group){
        try {
            group_get_latest.setString(1,group);
            ResultSet rs =group_get_latest.executeQuery();
            if(rs.next()){
                rs.updateInt(5, 1);
                rs.updateRow();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getWtd(Integer times){
        String wtd = "wt";
        for(int i=0;i<times;i++){
            wtd = wtd.concat("d");
        }
        return wtd;
    }

    protected Msg dealGroupMsg(RecvGroupMsg msg){
        String[] md5s = getImages(msg.getText());
        String group = msg.getGroup();
        String qq = msg.getQq();
        if(md5s.length == 0 )
            return Msg.Next(msg);
        ArrayList<Integer> wtd_times = water(group, qq, md5s);
        if(wtd_times.size() > 0) {
            SendGroupMsg smsg = new SendGroupMsg(group);
            if (wtd_times.size() == 1) {
                Integer times = wtd_times.get(0);
                if (times >= 1) {
                    smsg.setText(getWtd(times));
                }
                else return Msg.Next(msg);
            } else {
                String text = "";
                for (Integer times : wtd_times) {
                    if (times >= 1) {
                        String wtd = getWtd(times);
                        text = text.concat(wtd).concat(" ");
                    }
                    else text= text.concat("unwtd ");
                }
                smsg.setText(text);
            }
            return Msg.SendandNext(smsg);
        }
        else return Msg.Next(msg);
    }
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
    private WTD wtd;
    private Server server;
    private final String help_text;
    private final String acess_error_text;
    private final String format_error_text;
    private final String network_error_text;
    private final String ali_code;

    public Command(Config config, Server server){
        String[] strings = {"GroupMessage","PrivateMessage"};
        register(strings);
        this.server = server;
        this.wtd = new WTD(config.getDriver(), config.getUrl(), config.getUsername(), config.getPassword());
        this.fuDuJi = new FuDuJi();
        this.Admin = config.getAdmin();
        this.help_text =
                "直接at我聊天为图灵机器人回复。\n" +
                        "目前支持指令:\n" +
                        "所有人:\n" +
                        "/help 显示本帮助\n" +
                        "/code [poster] [syntax=cpp] [code] Ubuntu Paste。无任何参数返回上次代码链接。\n" +
                        "/ignore [qq = currentqq] 把最近一次发送的图片标记为表情包（不计入wtd），如果没有指定QQ号，默认为最近的所有图片最后一张。\n" +
                        "Admin:\n" +
                        "/fudu [on,off=on] 开启或者关闭群付读，如果无任何参数默认打开当前群付读。\n" +
                        "/wtd [on,off=on] 开启或者关闭wtd功能，如果无任何参数默认打开当前群wtd。\n" +
                        "/restart 重启bot\n" +
                        "/stop 关闭bot";
        this.acess_error_text = "权限错误";
        this.format_error_text = "格式错误";
        this.network_error_text = "网络错误，请重试";
        this.ali_code = "【支付宝】年终红包再加10亿！现在领取还有机会获得惊喜红包哦！长按复制此消息，打开最新版支付宝就能领取！JFlPDD13WX";
    }

    private String controlModule(String[] args, String qq, CQJModule module, String name){
        if(qq.equals(this.Admin)) {
            if (args.length == 1) {
                module.run();
                return name + "启动";
            }
            else if(args.length == 2){
                switch (args[1]) {
                    case "on":
                        module.run();
                        return name + "启动";
                    case "off":
                        module.stop();
                        return name + "关闭";
                    default:
                        return format_error_text;
                }
            }
            else return format_error_text;
        }
        else return acess_error_text;
    }

    protected Msg dealGroupMsg(RecvGroupMsg msg){
        String text = getPlainText(msg.getText());
        String[] args = text.split(" ");
        if(args.length == 0)
            return null;
        SendGroupMsg smsg = new SendGroupMsg(msg.getGroup());
        switch (args[0]){
            case "/ali":
                smsg.setText(this.ali_code);
                return Msg.SendandCut(smsg);
            case "/fd":
            case "/fudu":
                smsg.setText(controlModule(args, msg.getQq(), fuDuJi, "付读机"));
                break;
            case "/h":
            case "/help": // TODO:help [command]
                smsg.setText(help_text);
                break;
            case "/c":
            case "/code":
                args = text.split(" ", 4);
                if(args.length == 4){
                    Code cd = new Code(args[3], args[2], args[1]);
                    String url = cd.pasteIt();
                    if(url==null){
                        smsg.setText(network_error_text);
                    }
                    else{
                        smsg.setText(url);
                    }
                }
                else if(args.length == 1){
                    smsg.setText(Code.getLastUrl());
                }
                else smsg.setText(format_error_text);
                break;
            case "/ig":
            case "ignore":
                if(args.length == 1) {
                    wtd.tagEmoji(msg.getGroup());
                    smsg.setText("已忽略群内最近一次发送的图片");
                }
                else if(args.length == 2){
                    wtd.tagEmoji(msg.getGroup(),msg.getQq());
                    smsg.setText("已忽略当前用户最近一次发送的图片");
                }
                break;
            case "/wtd":
                smsg.setText(controlModule(args, msg.getQq(), wtd, "WTD鸡"));
                break;
            case "/restart":
                if(!msg.getQq().equals(Admin)){
                    smsg.setText(this.acess_error_text);
                    break;
                }
                server.torestart();
                smsg.setText("重启完成！");
                server.postMessage(smsg);
                return null;
            case "/stop":
                if(!msg.getQq().equals(Admin)){
                    smsg.setText(this.acess_error_text);
                    break;
                }
                smsg.setText("即将停止服务器。");
                server.postMessage(smsg);
                server.tostop();
                return null;
            default:
                return Msg.Next(smsg);
        }
        return Msg.SendandCut(smsg);
    }
}

public class Main {
    public static void go(Config config){
        Server c = Server.getServer(config.getTarget_port(),config.getServer_port());
        Command cmd = new Command(config, c);
        c.start();
    }
    public static void main(String[] args){
        Config config = Config.load("config.json");
        go(config);
    }
}