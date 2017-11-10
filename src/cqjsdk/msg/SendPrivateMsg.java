package cqjsdk.msg;

public class SendPrivateMsg extends Msg{
    private final String qq;
    private final String text;
    public SendPrivateMsg(String qq,String text){
        this.qq = qq;
        this.text = encode(text);
        this.prefix = "PrivateMessage";
    }

    @Override
    public String toString() {
        return prefix + " " + qq + " " + text;
    }
}
