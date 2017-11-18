package cqjsdk.server;

import cqjsdk.msg.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Dispatcher extends Thread {

    private interface dealable{
        Msg deal(Msg msg);
    }

    private static Dispatcher  dispatcher= new Dispatcher();
    private static Sender sender;
    private static ArrayList<CQJModule> module_list = CQJModule.getModuleList();
    private static BlockingQueue<Msg> msgq;
    private static Map<String, ArrayList<CQJModule>> module_map;

    private Dispatcher() { }
    public static Dispatcher getDispatcher(Sender sender){
        Dispatcher.sender = sender;
        msgq = new ArrayBlockingQueue<Msg>(4096);
        module_map = new HashMap<String, ArrayList<CQJModule>>();
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

    private void dealMsg(dealable dealer, Msg msg){
        for(CQJModule m : module_map.getOrDefault(prefix, new ArrayList<CQJModule>())) {
            Msg dealed_msg = dealer.deal(msg);
            if (dealed_msg != null) {
                if (dealed_msg.toSend()) {
                    sender.sendMsg(dealed_msg);
                }
                return dealed_msg.toNext();
            } else return false;
        }
    }

    private void dispatch_imp(Msg msg) {
        String prefix = msg.getPrefix();
        switch (prefix){
            // TODO:给所有的消息一个序号减少字符串比较？
            case "GroupMessage":
                dealMsg((Msg message) -> m.dealGroupMsg((RecvGroupMsg) message),msg)){

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
            for(String str : m.getToget()){
                module_map.putIfAbsent(str, new ArrayList<CQJModule>());
                module_map.get(str).add(m);
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
