package cqjsdk.server;

import cqjsdk.msg.*;

public class Server extends Thread {
    private Integer target_port;
    private Integer server_port;
    private Receiver recver;
    private static Server server = new Server();

    private Server(){}
    public static Server getServer(Integer target_port, Integer server_port){
        server.target_port = target_port;
        server.server_port = server_port;
        return server;
    }

    private void run_receiver(){
        this.recver = new Receiver(server_port, target_port);
        server.start();
    }

    private boolean initialized(){
        return this.recver.initialized();
    }

    public void run(){
        while(!initialized());
        ClientHelloMsg hellomsg = null;
        try {
            hellomsg = new ClientHelloMsg(server_port.toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        while(true){
            try {
                recver.sendMsg(hellomsg); // TODO:maybe sender isn't started when sending?
                Thread.sleep(60*4*1000);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
