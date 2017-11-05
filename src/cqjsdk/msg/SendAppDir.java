package cqjsdk.msg;

final public class SendAppDir extends Msg{
    SendAppDir(){
        this.prefix = "AppDirectory";
    }

    @Override
    public String toString() {
        return this.prefix;
    }
}
