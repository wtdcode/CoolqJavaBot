package cqjsdk;

import cqjsdk.server.*;
import cqjsdk.msg.*;

public class Client extends Thread {
    private Integer target_port;
    private Integer server_port;
    private Receiver server;
    private static Client client = new Client();

    private Client(){}
    public static Client getClient(Integer target_port, Integer server_port){
        client.target_port = target_port;
        client.server_port = server_port;
        client.run_receiver();
        return client;
    }

    private void run_receiver(){
        this.server = new Receiver(server_port, target_port);
        server.start();
    }

    public void run(){
        ClientHelloMsg hellomsg = null;
        try {
            hellomsg = new ClientHelloMsg(server_port.toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        while(true){
            try {
                server.sendMsg(hellomsg); // TODO:maybe sender isn't started when sending?
                Thread.sleep(60*4*1000);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
