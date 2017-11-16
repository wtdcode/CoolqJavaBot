package cqjsdk.msg;

final public class ServerHelloMsg extends Msg{
    private final String client_timeout;
    private final String prefix_size;
    private final String payload_szie;
    private final String frame_size;
    public ServerHelloMsg(String[] frag){
        this.prefix = frag[0];
        this.client_timeout = frag[1];
        this.prefix_size = frag[2];
        this.payload_szie = frag[3];
        this.frame_size = frag[4];
    }

    public String getClient_timeout() {
        return client_timeout;
    }

    public String getPrefix_size() {
        return prefix_size;
    }

    public String getPayload_szie() {
        return payload_szie;
    }

    public String getFrame_size() {
        return frame_size;
    }

    @Override
    public String toString() {
        return this.prefix + " " + this.client_timeout + " " + this.prefix_size + " " + this.payload_szie + " " + this.frame_size;
    }
}