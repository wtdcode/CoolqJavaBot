package cqjsdk.server;

import cqjsdk.msg.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
类名：CQJModule
作用：所有模块的抽象父类，提供了相应的回调函数。
 */
abstract public class CQJModule {

    protected Set<String> toget;
    protected Boolean running;

    private static String app_dir;
    private static ArrayList<CQJModule> modulelist = new ArrayList<CQJModule>();

    // 为了获得相应表情、at等特殊消息的正则表达式
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

    // 模块的控制开关
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

    // 注册函数
    final protected void register(String[] strings){
        modulelist.add(this);
        toget.addAll(Arrays.asList(strings));
    }

    // 实际实现函数
    private String[] getSth_imp(String text, Pattern pattern, Character left, Character right){
        ArrayList<String> strings = new ArrayList<String>();
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){
            String cqimg = matcher.group();
            strings.add(cqimg.substring(cqimg.indexOf(left)+1,cqimg.indexOf(right)));
        }
        return strings.toArray(new String[strings.size()]);
    }

    // 下面这些函数都是利用上面的正则表达式获取对应内容
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

    final Msg dealServerHello(ServerHelloMsg msg){
        // TODO:根据协议在这里储存几个变量？
        return null;
    }

    // 下面这些都是相应消息的回调函数
    protected Msg dealGroupMsg(RecvGroupMsg msg){ return null;}

    protected Msg dealDiscussMsg(RecvDiscussMsg msg){ return null;}

    protected Msg dealPrivateMsg(RecvPrivateMsg msg){ return null;}

}
