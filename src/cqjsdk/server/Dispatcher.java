package cqjsdk.server;

import cqjsdk.msg.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Dispatcher extends Thread {
    private static Dispatcher  dispatcher= new Dispatcher();
    private ArrayList<CQJModule> module_list = CQJModule.getModuleList();
    private BlockingQueue<Msg> msgq;
    private Map<String, ArrayList<CQJModule>> module_map;

    private Dispatcher() { }
    public static Dispatcher getDispatcher(BlockingQueue<Msg> msgq){
        dispatcher.msgq = msgq;
        dispatcher.module_map = new HashMap<String, ArrayList<CQJModule>>();
        dispatcher.parse_list();
        return dispatcher;
    }

    public boolean initialized(){
        return true;
    }

    private void dispatch(Msg msg) {
        switch (msg.getPrefix()){
            // TODO:给所有的消息一个序号减少字符串比较？
            case "GroupMessage":
                for(CQJModule m : module_map.getOrDefault("GroupMessage", new ArrayList<CQJModule>())) {
                    if (m.running()) {
                        m.dealGroupMsg((RecvGroupMsg) msg);
                    }
                }
                break;
            case "DiscussMessage":
                for(CQJModule m : module_list){
                    if(m.running()) {
                        m.dealDiscussMsg((RecvDiscussMsg) msg);
                    }
                }
                break;
            case "PrivateMessage":
                for(CQJModule m : module_list) {
                    if (m.running()) {
                        m.dealPivateMsg((RecvPrivateMsg) msg);
                    }
                }
                break;
            case "ServerHello":
                for(CQJModule m : module_list) {
                    if (m.running()) {
                        m.dealServerHello((ServerHelloMsg) msg);
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
                this.dispatch(msg);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
