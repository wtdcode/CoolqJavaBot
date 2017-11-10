package cqjsdk.msg;

public class RecvPrivateMsg extends Msg{
    private final String subtype;
    private final String qq;
    private final String raw_text;
    private final String decode_text;
    public RecvPrivateMsg(byte[] bytes, int len){
        String[] frag = new String(bytes, 0, len).split(" ");
        this.prefix = frag[0];
        this.subtype = frag[1];
        this.qq = frag[2];
        this.raw_text = frag[3];
        this.decode_text = decode(this.raw_text.getBytes(),this.raw_text.length());
    }

    public String getSubtype() {
        return subtype;
    }

    public String getQq() {
        return qq;
    }

    public String getText() {
        return decode_text;
    }

    @Override
    public String toString() {
        return prefix + " " + subtype + " " + raw_text;
    }
}
