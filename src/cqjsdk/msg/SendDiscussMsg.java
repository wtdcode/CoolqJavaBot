package cqjsdk.msg;

public class SendDiscussMsg extends Msg{
    private final String discuss;
    private final String text;
    public SendDiscussMsg(String discuss, String text){
        this.discuss = discuss;
        this.text = encode(text);
        this.prefix = "DiscussMessage";
    }

    @Override
    public String toString() {
        return prefix + " " + discuss + " " + text;
    }
}
