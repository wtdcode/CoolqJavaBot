package cqjsdk.server;

import cqjsdk.msg.*;

import cqjsdk.msg.*;
import cqjsdk.msg.*;

public class Formatter {
    private static  Formatter formatter = new Formatter();
    private Formatter(){ }
    public static Formatter getFormatter(){
        return formatter;
    }
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
