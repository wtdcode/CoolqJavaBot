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
        try{
            prefix = new String(bytes,0,len,"GB18030").split(" ")[0];
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        switch (prefix){
            case "GroupMessage":
                return new RecvGroupMsg(bytes, len);
            case "ServerHello":
                return new ServerHelloMsg(bytes, len);
            default:
                return null;
        }
    }

    public byte[] FormatSend(Msg msg){
        return msg.encode();
    }

}
