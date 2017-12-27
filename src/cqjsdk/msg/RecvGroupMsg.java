package cqjsdk.msg;

final public class RecvGroupMsg extends Msg{
    private final String subtype;
    private final String group;
    private final String qq;
    private final String from_anonymous;
    private final String raw_text;
    private final String decode_text;

    public RecvGroupMsg(String[] frag) {
        this.prefix = frag[0];
        this.subtype = frag[1];
        this.group = frag[2];
        this.qq = frag[3];
        this.from_anonymous = frag[4];
        this.raw_text = frag[5];
        this.decode_text = decode(this.raw_text);
    }

    public String getSubtype() {
        return subtype;
    }

    public String getGroup() {
        return group;
    }

    public String getQq() {
        return qq;
    }

    public String getFrom_anonymous() {
        return from_anonymous;
    }

    public String getText() {
        return decode_text;
    }

    @Override
    public String toString() {
        return this.prefix + " " + this.subtype + " " + this.group + " " + this.qq + " " + this.from_anonymous + " " + this.raw_text;
    }
}
