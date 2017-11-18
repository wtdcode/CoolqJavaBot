package cqjsdk.msg;

public class SendPrivateMsg extends Msg{
    private String qq;
    private String text;

    public SendPrivateMsg(String qq){
        this.qq = qq;
        this.text = encode("null");
        this.prefix = "PrivateMessage";
    }

    public SendPrivateMsg(String qq,String text){
        this(qq);
        this.text = encode(text);
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public void setText(String text) {
        this.text = encode(text);
    }

    public String getText() {
        return decode(text);
    }

    @Override
    public String toString() {
        return prefix + " " + qq + " " + text;
    }
}
