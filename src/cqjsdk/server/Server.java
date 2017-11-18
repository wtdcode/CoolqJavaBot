package cqjsdk.server;

import cqjsdk.msg.*;

import java.net.DatagramSocket;

public class Server extends Thread {
    private static DatagramSocket server_socket;
    private static Integer target_port;
    private static Integer server_port;
    private static Receiver receiver;
    private static Sender sender;
    private static Dispatcher dispatcher;
    private static Server server = new Server();

    private Server(){}
    public static Server getServer(Integer target_port, Integer server_port){
        Server.receiver = null;
        Server.sender = null;
        Server.target_port = target_port;
        Server.server_port = server_port;
        try {
            Server.server_socket = new DatagramSocket(server_port);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return server;
    }

    // TODO:准备加入重启功能
    void restart(){

    }
    // TODO:准备加入stop功能
    void stop_server(){

    }

    private void run_dispatcher(){
        dispatcher = Dispatcher.getDispatcher(sender);
        dispatcher.start();
    }

    private void run_receiver(){
        receiver = Receiver.getReceiver(server_socket,dispatcher);
        receiver.start();
    }

    private  void run_sender(){
        sender = Sender.getSender(server_socket,target_port);
        sender.start();
    }

    private void initialize(){
        run_sender();
        run_dispatcher();
        run_receiver();
    }

    public void run(){
        initialize();
        ClientHelloMsg hellomsg = null;
        SendAppDir sendAppDirmsg = null;
        try {
            hellomsg = new ClientHelloMsg(server_port.toString());
            sendAppDirmsg = new SendAppDir();
            sender.sendMsg(sendAppDirmsg);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        while(true){
            try {
                // TODO:用Timer代替？
                sender.sendMsg(hellomsg);
                Thread.sleep(60*4*1000);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
