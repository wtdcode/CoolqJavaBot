package cqjsdk.server;

import cqjsdk.msg.*;
import cqjsdk.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Receiver extends Thread{ // TODO:Maybe Server?
    private DatagramSocket server;
    private BlockingQueue<Msg> msgq;
    private Integer target_port;
    private Sender sender;

    public Receiver(Integer server_port, Integer target_port) {
        try{
            this.msgq = new ArrayBlockingQueue<Msg>(4096);
            this.server = new DatagramSocket(server_port);
            this.target_port = target_port;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void run_dispatcher(){
        // TODO:记得重构（改成一个Server封装？）
        Thread dispatcher = Dispatcher.getDispatcher(msgq);
        dispatcher.start();
    }


    private  void run_sender(){
        this.sender = Sender.getSender(this.server,target_port);
        this.sender.start();
    }

    public void sendMsg(Msg msg){
        sender.sendMsg(msg);
    }

    public void run(){
        run_sender();
        run_dispatcher();
        byte[] buf = new byte[4096];
        Formatter formatter = Formatter.getFormatter();
        Msg msg;
        try {
            while(true){
                DatagramPacket msgpacket = new DatagramPacket(buf, buf.length);
                server.receive(msgpacket);
                msg = formatter.FormatRecv(msgpacket.getData(), msgpacket.getLength());
                msgq.put(msg);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}