package cqjsdk.msg;

final public class ClientHelloMsg extends Msg{
    protected final String port;
    public ClientHelloMsg(String port){
        this.prefix = "ClientHello";
        this.port = port;
    }

    @Override
    public String toString() {
        return this.prefix + " " + this.port;
    }
}