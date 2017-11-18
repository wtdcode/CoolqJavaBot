package cqjsdk.msg;

public class SendDiscussMsg extends Msg{
    private  String discuss;
    private String text;

    public SendDiscussMsg(String discuss){
        this.discuss = discuss;
        this.prefix = "DiscussMessage";
        this.text = encode("null");
    }

    public SendDiscussMsg(String discuss, String text){
        this(discuss);
        this.text = encode(text);
    }

    public String getDiscuss() {
        return discuss;
    }

    public void setDiscuss(String discuss) {
        this.discuss = discuss;
    }

    public String getText() {
        return decode(text);
    }

    public void setText(String text) {
        this.text = encode(text);
    }

    @Override
    public String toString() {
        return prefix + " " + discuss + " " + text;
    }
}
