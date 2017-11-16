package cqjsdk.msg;

public final class RecvDiscussMsg extends Msg{
    private final String subtype;
    private final String discuss;
    private final String qq;
    private final String raw_text;
    private final String decode_text;
    public RecvDiscussMsg(byte[] bytes, int len){
        String[] frag = new String(bytes,0,len).split(" ");
        this.prefix = frag[0];
        this.subtype = frag[1];
        this.discuss = frag[2];
        this.qq = frag[3];
        this.raw_text = frag[4];
        this.decode_text = decode(this.raw_text);
    }

    public String getSubtype() {
        return subtype;
    }

    public String getDiscuss() {
        return discuss;
    }

    public String getQq() {
        return qq;
    }

    public String getText() {
        return decode_text;
    }

    @Override
    public String toString() {
        return prefix + " " + subtype + " " + qq + " " + raw_text;
    }
}
