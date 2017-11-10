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
    protected Set<String> toget;
    protected Boolean started;
    protected Pattern CQIMG_PATTERN = Pattern.compile("\\[CQ:image,file=(.+?)\\]");
    protected Pattern CQAT_PATTERN = Pattern.compile("\\[CQ:at,qq=(\\d+?)\\]");

    protected CQJModule(){
        started = true; // TODO:改成收到ServerHello再started
        toget = new HashSet<String>();
    }

    public static ArrayList<CQJModule> getModuleList(){
        return modulelist;
    }

    public void register(String[] strings){
        modulelist.add(this);
        this.toget.addAll(Arrays.asList(strings));
    }

    protected void dealServerHello(ServerHelloMsg msg){
        this.started = true;
    }

    protected void dealGroupMsg(RecvGroupMsg msg){ }

    protected void dealDiscussMsg(RecvDiscussMsg msg){}

    protected void dealPivateMsg(RecvPrivateMsg msg){}

    protected void sendMsg(Msg msg) {
        Sender.sendMsg(msg);
    }
}
