package cqjsdk;

import cqjsdk.msg.*;
import cqjsdk.server.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

abstract public class CQJModule {

    private static ArrayList<CQJModule> modulelist = new ArrayList<CQJModule>();
    //TODO:获得Appdir并存储？
    protected Set<String> toget;
    protected Pattern CQIMG_PATTERN = Pattern.compile("\\[CQ:image,file=(.+?)\\]");
    protected Pattern CQAT_PATTERN = Pattern.compile("\\[CQ:at,qq=(\\d+?)\\]");

    protected CQJModule(){
        toget = new HashSet<String>();
    }

    public static ArrayList<CQJModule> getModuleList(){
        return modulelist;
    }

    public void register(String[] strings){
        modulelist.add(this);
        this.toget.addAll(Arrays.asList(strings));
    }

    final void dealServerHello(ServerHelloMsg msg){
        // TODO:根据协议在这里储存几个变量？
    }

    protected void dealGroupMsg(RecvGroupMsg msg){ }

    protected void dealDiscussMsg(RecvDiscussMsg msg){}

    protected void dealPivateMsg(RecvPrivateMsg msg){}

    protected void sendMsg(Msg msg) {
        Sender.sendMsg(msg);
    }
}
