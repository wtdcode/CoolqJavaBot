package cqjsdk.msg;

import java.util.*;


abstract public class Msg{

    String prefix;

    public Msg(){
        prefix = "null";
    }

    @Override
    abstract  public String toString();

    public String getPrefix() {
        return prefix;
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
