package cqjsdk.server;

import cqjsdk.msg.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Sender extends Thread{
    private BlockingQueue<Msg> sendq;
    private Integer target_port;
    private DatagramSocket socket;
    private Boolean toStop;
    private Boolean stopped;

    Sender(DatagramSocket server, Integer target_port){
        this.target_port = target_port;
        this.socket = server;
        this.sendq = new ArrayBlockingQueue<Msg>(4096);
        toStop = false;
    }

    public Boolean stopped(){
        return stopped;
    }

    void sendMsg(Msg msg){
        try {
            sendq.offer(msg, 10, TimeUnit.SECONDS);
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
        catch (SocketException | NullPointerException stopEx){
            if(!toStop) {
                stopEx.printStackTrace();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        msg.sent();
    }

    public void run(){
        Msg msg;
        try{
            while(!toStop){
                msg = sendq.poll(10, TimeUnit.SECONDS);
                if(msg != null) {
                    sendMsg_imp(msg);
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        stopped = true;
    }

    public void die(){
        toStop = true;
        socket.close();
    }
}
