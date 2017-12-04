package cqjsdk.server;

import cqjsdk.msg.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Dispatcher extends Thread {

    private interface dealable{
        Msg deal(CQJModule module, Msg msg);
    }

    private static Dispatcher  dispatcher= new Dispatcher();
    private static Sender sender;
    private static ArrayList<CQJModule> module_list;
    private static Integer last_module_list_size=0;
    private static BlockingQueue<Msg> msgq;
    private static Map<String, ArrayList<CQJModule>> module_map;

    private Dispatcher() { }
    public static Dispatcher getDispatcher(Sender sender){
        Dispatcher.sender = sender;
        msgq = new ArrayBlockingQueue<Msg>(4096);
        module_map = new HashMap<String, ArrayList<CQJModule>>();
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
        String prefix = msg.getPrefix();
        for(CQJModule m : module_map.getOrDefault(prefix, new ArrayList<CQJModule>())) {
            if(m.running()) {
                Msg dealed_msg = dealer.deal(m, msg);
                if (dealed_msg != null) {
                    if (dealed_msg.toSend()) {
                        sender.sendMsg(dealed_msg);
                    }
                    if (!dealed_msg.toNext()) {
                        break;
                    }
                }
            }
        }
    }

    private void dispatch_imp(Msg msg) {
        String prefix = msg.getPrefix();
        switch (prefix){
            // TODO:给所有的消息一个序号减少字符串比较？
            // TODO:给module_list再加一维能按群号disapath?
            case "GroupMessage":
                dealMsg((CQJModule module, Msg message) -> module.dealGroupMsg((RecvGroupMsg) message),msg);
                break;
            case "DiscussMessage":
                dealMsg((CQJModule module, Msg message) -> module.dealDiscussMsg((RecvDiscussMsg) message),msg);
                break;
            case "PrivateMessage":
                dealMsg((CQJModule module, Msg message) -> module.dealPrivateMsg((RecvPrivateMsg) message),msg);
                break;
            case "ServerHello":
                dealMsg((CQJModule module, Msg message) -> module.dealServerHello((ServerHelloMsg) message),msg);
                break;
            case "SrvAppDirectory":
                CQJModule.setApp_dir(((RecvAppDir)msg).getApp_dir());
            default:
                break;
        }
    }

    private static void parse_list(){
        module_list = CQJModule.getModuleList();
        if(last_module_list_size != module_list.size()) {
            for (CQJModule m : module_list) {
                for (String str : m.getToget()) {
                    module_map.putIfAbsent(str, new ArrayList<CQJModule>());
                    module_map.get(str).add(m);
                }
            }
        }
        last_module_list_size = module_list.size();
    }

    public void run(){
        try {
            while (true) {
                Msg msg = msgq.take();
                this.dispatch_imp(msg);
                parse_list();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
