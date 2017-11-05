package cqjsdk.msg;

final public class RecvAppDir extends Msg{
    protected final String app_dir;
    RecvAppDir(byte[] bytes, int len){
        String[] frag = new String(bytes,0,len).split(" ");
        this.prefix = frag[0];
        this.app_dir = frag[1];
    }

    public String getApp_dir() {
        return app_dir;
    }

    @Override
    public String toString() {
        return this.prefix + " " + this.app_dir;
    }
}