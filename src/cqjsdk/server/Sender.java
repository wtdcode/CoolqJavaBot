package cqjsdk.server;

import cqjsdk.msg.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Sender extends Thread{
    private static Sender sender = new Sender();
    private static BlockingQueue<Msg> sendq;
    private static Integer target_port;
    private static DatagramSocket socket;
    private Sender(){ }

    public static Sender getSender(DatagramSocket server, Integer target_port){
        Sender.target_port = target_port;
        Sender.socket = server;
        Sender.sendq = new ArrayBlockingQueue<Msg>(4096);
        return sender;
    }

    public static void sendMsg(Msg msg){
        try {
            sendq.put(msg);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void sendMsg_imp(Msg msg){
        try {
            byte[] buf = msg.encode();
            InetAddress ip = InetAddress.getLoopbackAddress();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,ip,target_port);
            socket.send(packet);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void run(){
        Msg msg;
        try{
            while(true){
                msg = sendq.take();
                sendMsg_imp(msg);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
