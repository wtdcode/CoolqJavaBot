package cqjsdk.msg;

import java.util.*;

/*
类名：Msg
作用：所有服务器内部消息的公共父类，提供常用的函数。
 */
abstract public class Msg{

    protected String prefix; // 前缀
    protected Boolean to_next; // 是否继续传递给下个模块
    protected Boolean to_send; // 是否继续发送
    protected Boolean sended; // 是否已经发送

    public Msg(){
        prefix = "null";
        to_next = true;
        to_send = false;
        sended = false;
    }

    public Boolean sended() {
        return sended;
    }

    public void sent(){
        sended  = true;
    }

    @Override
    abstract  public String toString();

    public String getPrefix() {
        return prefix;
    }

    public void setNext(Boolean to_next){
        this.to_next = to_next;
    }

    public void setSend(Boolean to_send){
        this.to_send = to_send;
    }

    public Boolean toNext() {
        return to_next;
    }

    public Boolean toSend() {
        return to_send;
    }

    public static Msg SendandNext(Msg msg){
        msg.setNext(true);
        msg.setSend(true);
        return msg;
    }

    // 下面这三个函数都是Dispatcher对模块返回的消息的处理，是否继续传递给下一个模块和是否发送到CQ
    public static Msg Next(Msg msg){
        msg.setNext(true);
        msg.setSend(false);
        return msg;
    }

    public static Msg NextWithoutSend(Msg msg){
        return Msg.Next(msg);
    }

    public static Msg SendandCut(Msg msg){
        msg.setNext(false);
        msg.setSend(true);
        return msg;
    }

    // text编码和解码函数
    public byte[] encode(){
        return this.toString().getBytes();
    }

    protected String decode(String raw_text){
        String result = "";
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            result = new String(decoder.decode(raw_text), "GB18030");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    protected String encode(String text){
        String encode_text = null;
        try{
            Base64.Encoder encoder = Base64.getEncoder();
            encode_text = encoder.encodeToString(text.getBytes("GB18030"));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return encode_text;
    }

}


/*
final class RecvPrivateMessage extends Msg {
    protected final String subtype;
    protected final String qq;
    protected final String text;
    public RecvPrivateMessage(byte[] bytes, Integer len){
        String[]
    }
}*/
