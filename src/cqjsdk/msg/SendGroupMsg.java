package cqjsdk.msg;

import java.util.Base64;

final public class SendGroupMsg extends Msg{
    private String group;
    private String text;

    public SendGroupMsg(String group, String text) {
        // TODO:add handle
        this.group = group;
        this.text = encode(text);
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