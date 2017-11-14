package cqjsdk.server;

import cqjsdk.msg.*;

import java.net.DatagramSocket;

public class Server extends Thread {
    private DatagramSocket server_socket;
    private Integer target_port;
    private Integer server_port;
    private Receiver receiver;
    private Sender sender;
    private static Server server = new Server();

    private Server(){}
    public static Server getServer(Integer target_port, Integer server_port){
        server.receiver = null;
        server.sender = null;
        server.target_port = target_port;
        server.server_port = server_port;
        try {
            server.server_socket = new DatagramSocket(server_port);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return server;
    }

    private void run_receiver(){
        this.receiver = new Receiver(this.server_socket);
        this.receiver.start();
    }

    private  void run_sender(){
        this.sender = Sender.getSender(this.server_socket,target_port);
        this.sender.start();
    }

    private boolean initialized(){
        return this.receiver!=null &&
                this.sender != null &&
                this.receiver.initialized() &&
                this.sender.initialized();
    }

    public void run(){
        run_receiver();
        run_sender();
        while(!this.initialized());
        ClientHelloMsg hellomsg = null;
        try {
            hellomsg = new ClientHelloMsg(server_port.toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        while(true){
            try {
                Sender.sendMsg(hellomsg);
                Thread.sleep(60*4*1000);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
