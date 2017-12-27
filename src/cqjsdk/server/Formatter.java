package cqjsdk.server;

import cqjsdk.msg.*;

import cqjsdk.msg.*;
import cqjsdk.msg.*;

/*
类名：Formatter
作用：根据接收到的数据格式化消息。
 */
public class Formatter {
    private static  Formatter formatter = new Formatter();
    private Formatter(){ }
    public static Formatter getFormatter(){
        return formatter;
    }

    // 根据接收到的内容格式化
    public Msg FormatRecv(byte[] bytes, int len){
        String prefix = "";
        String[] frag = null;
        try{
            frag = new String(bytes,0,len,"GB18030").split(" ");
            prefix = frag[0];
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        switch (prefix){
            case "GroupMessage":
                return new RecvGroupMsg(frag);
            case "ServerHello":
                return new ServerHelloMsg(frag);
            case "DiscussMessage":
                return new RecvDiscussMsg(frag);
            case "PrivateMessage":
                return new RecvPrivateMsg(frag);
            case "SrvAppDirectory":
                return new RecvAppDir(frag);
            default:
                return null;
        }
    }

    public byte[] FormatSend(Msg msg){
        return msg.encode();
    }

}
