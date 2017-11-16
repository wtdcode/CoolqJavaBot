package cqjsdk.msg;

final public class RecvAppDir extends Msg{
    protected final String raw_app_dir;
    protected final String app_dir;
    public RecvAppDir(byte[] bytes, int len){
        String[] frag = new String(bytes,0,len).split(" ");
        this.prefix = frag[0];
        this.raw_app_dir = frag[1];
        this.app_dir = decode(this.raw_app_dir);
    }

    public String getApp_dir() {
        return app_dir;
    }

    @Override
    public String toString() {
        return this.prefix + " " + this.raw_app_dir;
    }
}