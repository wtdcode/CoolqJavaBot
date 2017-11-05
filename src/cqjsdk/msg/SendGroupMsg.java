package cqjsdk.msg;

import java.util.Base64;

final public class SendGroupMsg extends Msg{
    protected String group;
    protected String text;

    public SendGroupMsg(String group, String text) {
        // TODO:add handle
        this.group = group;
        String encode_text = "";
        try{
            Base64.Encoder encoder = Base64.getEncoder();
            encode_text = encoder.encodeToString(text.getBytes("GB18030"));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        this.text = encode_text;
        this.prefix = "GroupMessage";
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) { this.group = group;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.prefix + " " + this.group + " " + this.text;
    }
}