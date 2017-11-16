package cqjsdk.msg;

final public class SendAppDir extends Msg{
    public SendAppDir(){
        this.prefix = "AppDirectory";
    }

    @Override
    public String toString() {
        return this.prefix;
    }
}
