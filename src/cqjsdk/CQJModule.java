package cqjsdk;

import cqjsdk.msg.*;
import cqjsdk.server.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

abstract public class CQJModule {

    private static ArrayList<CQJModule> modulelist = new ArrayList<CQJModule>();
    protected Set<String> toget;
    protected Boolean started;

    protected CQJModule(){
        started = true;
        toget = new HashSet<String>();
    }

    public static ArrayList<CQJModule> getModuleList(){
        return modulelist;
    }

    public void register(String[] strings){
        modulelist.add(this);
        this.toget.addAll(Arrays.asList(strings));
    }

    protected void dealServerHello(Msg msg){
        this.started = true;
    }

    protected void sendMsg(Msg msg) {
        Sender.sendMsg(msg);
    }

    protected void dealGroupMsg(RecvGroupMsg msg){
    }
}
