package cqjsdk.server;

import cqjsdk.msg.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/*
类名：Dispatcher
作用：根据消息的种类分发给相应的模块。
 */
public class Dispatcher extends Thread {

    // 处理函数的抽象接口
    private interface dealable{
        Msg deal(CQJModule module, Msg msg);
    }

    private Sender sender;
    private ArrayList<CQJModule> module_list;
    private Integer last_module_list_size=0;
    private BlockingQueue<Msg> msgq;
    private Map<String, ArrayList<CQJModule>> module_map;
    private Boolean toStop;
    private Boolean stopped;

    Dispatcher(Sender sender){
        this.sender = sender;
        msgq = new ArrayBlockingQueue<Msg>(4096);
        module_map = new HashMap<String, ArrayList<CQJModule>>();
        toStop = false;
        stopped = false;
    }

    public Boolean stopped(){
        return stopped;
    }

    void dispatch(Msg msg){
        try {
            msgq.offer(msg, 10, TimeUnit.SECONDS);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // 抽象的处理函数
    private void dealMsg(dealable dealer, Msg msg){
        String prefix = msg.getPrefix();
        for(CQJModule m : module_map.getOrDefault(prefix, new ArrayList<CQJModule>())) {
            if(m.running()) {
                Msg dealed_msg = dealer.deal(m, msg);
                if (dealed_msg != null) {
                    if (dealed_msg.toSend()) {
                        try {
                            sender.sendMsg(dealed_msg);
                        }
                        catch (NullPointerException ex){
                            if(toStop) {
                                return;
                            }
                            else{
                                ex.printStackTrace();
                            }
                        }
                    }
                    if (!dealed_msg.toNext()) {
                        break;
                    }
                }
                else{
                    break;
                }
            }
        }
    }

    // 实际分发函数的实现
    private void dispatch_imp(Msg msg) {
        String prefix = msg.getPrefix();
        switch (prefix){
            // TODO:给所有的消息一个序号减少字符串比较？
            // TODO:给module_list再加一维能按群号dispatch?
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

    // 动态获得所有模块列表（转发列表）
    private void parse_list(){
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
            while (!toStop) {
                Msg msg = msgq.poll(10, TimeUnit.SECONDS);
                if(msg != null) {
                    dispatch_imp(msg);
                }
                parse_list();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        stopped = true;
    }

    public void die(){
        toStop = true;
    }
}
