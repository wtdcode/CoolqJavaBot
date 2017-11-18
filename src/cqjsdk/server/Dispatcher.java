package cqjsdk.server;

import cqjsdk.msg.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Dispatcher extends Thread {
    private static Dispatcher  dispatcher= new Dispatcher();
    private ArrayList<CQJModule> module_list = CQJModule.getModuleList();
    private BlockingQueue<Msg> msgq;
    private Map<String, ArrayList<CQJModule>> module_map;

    private Dispatcher() { }
    public static Dispatcher getDispatcher(){
        dispatcher.msgq = new ArrayBlockingQueue<Msg>(4096);
        dispatcher.module_map = new HashMap<String, ArrayList<CQJModule>>();
        dispatcher.parse_list();
        return dispatcher;
    }

    void dispatch(Msg msg){
        try {
            msgq.put(msg);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void dispatch_imp(Msg msg) {
        switch (msg.getPrefix()){
            // TODO:给所有的消息一个序号减少字符串比较？
            case "GroupMessage":
                for(CQJModule m : module_map.getOrDefault("GroupMessage", new ArrayList<CQJModule>())) {
                    if (m.running()) {
                        if(m.dealGroupMsg((RecvGroupMsg) msg)){
                            break;
                        }
                    }
                }
                break;
            case "DiscussMessage":
                for(CQJModule m : module_list){
                    if(m.running()) {
                        if(m.dealDiscussMsg((RecvDiscussMsg) msg)){
                            break;
                        }
                    }
                }
                break;
            case "PrivateMessage":
                for(CQJModule m : module_list) {
                    if (m.running()) {
                        if(m.dealPivateMsg((RecvPrivateMsg) msg)){
                            break;
                        }
                    }
                }
                break;
            case "ServerHello":
                for(CQJModule m : module_list) {
                    if (m.running()) {
                        if(m.dealServerHello((ServerHelloMsg) msg)){
                            break;
                        }
                    }
                }
                break;
            case "SrvAppDirectory":
                CQJModule.setApp_dir(((RecvAppDir)msg).getApp_dir());
            default:
                break;
        }
    }

    private void parse_list(){
        for(CQJModule m : module_list){
            for(String str : m.toget){
                this.module_map.putIfAbsent(str, new ArrayList<CQJModule>());
                this.module_map.get(str).add(m);
            }
        }
    }

    public void run(){
        try {
            while (true) {
                Msg msg = msgq.take();
                this.dispatch_imp(msg);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
