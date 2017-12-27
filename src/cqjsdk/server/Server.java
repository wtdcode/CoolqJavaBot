package cqjsdk.server;

import cqjsdk.msg.*;

import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends Thread {
    private static DatagramSocket server_socket;
    private static Integer target_port;
    private static Integer server_port;
    private static Receiver receiver;
    private static Sender sender;
    private static Dispatcher dispatcher;
    private static Server server = new Server();
    private static ClientHelloMsg helloMsg;

    private static final int SERVER_STOPPED = 1;
    private static final int SERVER_STARTED = 0;
    private static int serverState = SERVER_STOPPED;

    private Server(){}
    public static Server getServer(Integer target_port, Integer server_port){
        Server.receiver = null;
        Server.sender = null;
        Server.target_port = target_port;
        Server.server_port = server_port;
        return server;
    }

    public void torestart(){
        all_die();
        initialize();
        serverState = SERVER_STARTED;
    }

    public void tostop(){
        all_die();
        serverState = SERVER_STOPPED;
    }

    public Boolean stopped(){
        if(serverState == SERVER_STOPPED){
            return true;
        }
        else{
            return false;
        }
    }

    private void run_dispatcher(){
        dispatcher = new Dispatcher(sender);
        dispatcher.start();
    }

    private void run_receiver(){
        receiver = new Receiver(server_socket,dispatcher);
        receiver.start();
    }

    private  void run_sender(){
        sender = new Sender(server_socket,target_port);
        sender.start();
    }

    private void initialize(){
        try {
            Server.server_socket = new DatagramSocket(server_port);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        run_sender();
        run_dispatcher();
        run_receiver();
    }

    private void all_die(){
        sender.die();
        dispatcher.die();
        receiver.die();
    }

    // 这个接口会在以后会有所改变
    // 具体就是修改Msg继承结构，分为RecvMsg和SendMsg
    public void postMessage(Msg msg){
        sender.sendMsg(msg);
    }

    public void run(){
        initialize();
        SendAppDir sendAppDirmsg = null;
        helloMsg = new ClientHelloMsg(server_port.toString());
        sendAppDirmsg = new SendAppDir();
        sender.sendMsg(sendAppDirmsg);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sender.sendMsg(helloMsg);
            }
        },0,4*60*1000);
        serverState = SERVER_STARTED;
    }
}
