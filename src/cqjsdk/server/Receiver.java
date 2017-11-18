package cqjsdk.server;

import cqjsdk.msg.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Receiver extends Thread{

    private static BlockingQueue<Msg> msgq;
    private static DatagramSocket server;
    private static Dispatcher dispatcher;
    private static Receiver receiver = new Receiver();

    private Receiver(){}

    static Receiver getReceiver(DatagramSocket server, Dispatcher dispatcher) {
        try{
            Receiver.msgq = new ArrayBlockingQueue<Msg>(4096);
            Receiver.server = server;
            Receiver.dispatcher = dispatcher;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return receiver;
    }

    public void run(){
        byte[] buf = new byte[65536];
        Formatter formatter = Formatter.getFormatter();
        Msg msg;
        try {
            while(true){
                DatagramPacket msgpacket = new DatagramPacket(buf, buf.length);
                server.receive(msgpacket);
                msg = formatter.FormatRecv(msgpacket.getData(), msgpacket.getLength());
                dispatcher.dispatch(msg);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}