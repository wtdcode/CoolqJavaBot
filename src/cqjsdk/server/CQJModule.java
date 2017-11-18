package cqjsdk.server;

import cqjsdk.msg.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class CQJModule {

    //TODO:获得Appdir并存储？
    protected Set<String> toget;
    protected Boolean running;

    private static String app_dir;
    private static ArrayList<CQJModule> modulelist = new ArrayList<CQJModule>();

    protected Pattern CQIMG_PATTERN = Pattern.compile("\\[CQ:image,file=(.+?)\\]");
    protected Pattern CQAT_PATTERN = Pattern.compile("\\[CQ:at,qq=(\\d+?)\\]");
    protected Pattern CQFACE_PATTERN = Pattern.compile("\\[CQ:face,id=(\\d+?)\\]");
    protected Pattern CQBAFACE_PATTERN = Pattern.compile("\\[CQ:bface\\]");
    protected Pattern CQEMOJI_PATTERN = Pattern.compile("\\[CQ:face,id=(\\d+?)\\]");

    protected CQJModule(){
        toget = new HashSet<String>();
        this.running = true;
    }

    public Boolean running(){
        return this.running;
    }

    public void run(){
        this.running = true;
    }

    public void stop(){
        this.running = false;
    }

    public void switchstat(){
        this.running = !this.running;
    }

    static ArrayList<CQJModule> getModuleList(){
        return modulelist;
    }

    public Set<String> getToget() {
        return toget;
    }

    final protected void register(String[] strings){
        modulelist.add(this);
        toget.addAll(Arrays.asList(strings));
    }
    private String[] getSth_imp(String text, Pattern pattern, Character left, Character right){
        ArrayList<String> strings = new ArrayList<String>();
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            String cqimg = matcher.group();
            strings.add(cqimg.substring(cqimg.indexOf(left)+1,cqimg.indexOf(right)));
        }
        return strings.toArray(new String[strings.size()]);
    }

    final protected String[] getImages(String text){
        return getSth_imp(text, CQIMG_PATTERN, '=', '.');
    }

    final protected String[] getAts(String text){
        return getSth_imp(text, CQAT_PATTERN,'=',']');
    }

    final protected String[] getFaces(String text){
        return getSth_imp(text, CQFACE_PATTERN, '=', ']');
    }

    final protected String[] getEmojis(String text){
        return getSth_imp(text, CQEMOJI_PATTERN, '=', ']');
    }

    final protected String getPlainText(String text){
        return text.replaceAll("\\[.+?\\]","");
    }

    // 获取的是App目录不是Coolq目录，不过或许以后可以用来得到Coolq目录？
    static void setApp_dir(String app_dir) {
        CQJModule.app_dir = app_dir;
    }

    final Boolean dealServerHello(ServerHelloMsg msg){
        // TODO:根据协议在这里储存几个变量？
        return true;
    }

    protected SendGroupMsg dealGroupMsg(RecvGroupMsg msg){ return null;}

    protected SendDiscussMsg dealDiscussMsg(RecvDiscussMsg msg){ return null;}

    protected SendPrivateMsg dealPivateMsg(RecvPrivateMsg msg){ return null;}

}
