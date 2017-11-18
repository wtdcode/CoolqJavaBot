package cqjsdk.msg;

import java.util.*;


abstract public class Msg{

    protected String prefix;
    protected Boolean to_next;
    protected Boolean to_send;

    public Msg(){
        prefix = "null";
        to_next = true;
        to_send = false;
    }

    @Override
    abstract  public String toString();

    public String getPrefix() {
        return prefix;
    }

    public void setToNext(Boolean to_next){
        this.to_next = to_next;
    }

    public void setToSend(Boolean to_send){
        this.to_send = to_send;
    }

    public Boolean toNext() {
        return to_next;
    }

    public Boolean toSend() {
        return to_send;
    }

    public byte[] encode(){
        return this.toString().getBytes();
    }

    protected String decode(String raw_text){
        // TODO: add exception handle
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
