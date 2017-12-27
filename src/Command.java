import cqjsdk.msg.Msg;
import cqjsdk.msg.RecvGroupMsg;
import cqjsdk.msg.SendGroupMsg;
import cqjsdk.server.CQJModule;
import cqjsdk.server.Logger;
import cqjsdk.server.Server;

/*
名称：Command
作用：bot模块的核心控制模块。
 */
public class Command extends CQJModule {
    private final String Admin;
    private FuDuJi fuDuJi;
    private WTD wtd;
    private Server server;
    private final String help_text;
    private final String acess_error_text;
    private final String format_error_text;
    private final String network_error_text;
    private final String ali_code;

    // 注册模块
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
        Logger.Log("CMD模块加载");
    }

    // 对于一些仅仅是on off的模块进行逻辑抽象
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

    // 继承的消息处理回调函数
    protected Msg dealGroupMsg(RecvGroupMsg msg){
        String text = getPlainText(msg.getText());
        String[] args = text.split(" ");
        if(args.length == 0)
            return null;
        SendGroupMsg smsg = new SendGroupMsg(msg.getGroup());
        // 查看是否是命令
        switch (args[0]){
            case "/ali":
                smsg.setText(this.ali_code);
                return Msg.SendandCut(smsg);
            case "/fd":
            case "/fudu":
                smsg.setText(controlModule(args, msg.getQq(), fuDuJi, "付读机"));
                break;
            case "/h":
            case "/help":
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
            case "/ignore":
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
                server.postMessage(smsg); // 这里之所以单独用postMessage发送，是因为这时候语境已经不是现在server的语境了
                return null;
            case "/stop":
                if(!msg.getQq().equals(Admin)){
                    smsg.setText(this.acess_error_text);
                    break;
                }
                smsg.setText("即将停止服务器。");
                server.postMessage(smsg); // 理由同上
                while(!smsg.sended()); // 应该有更优雅的实现方式
                server.tostop();
                return null;
            default:
                return Msg.Next(smsg); // 如果不是指令就不发送并交给下一个模块处理
        }
        return Msg.SendandCut(smsg); // 默认截断并发送
    }
}