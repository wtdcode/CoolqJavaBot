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

    static Receiver getReceiver(DatagramSocket server) {
        try{
            Receiver.msgq = new ArrayBlockingQueue<Msg>(4096);
            Receiver.server = server;
            Receiver.dispatcher = null;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return receiver;
    }

    Dispatcher getDispatcher(){
        return dispatcher;
    }

    boolean initialized(){
        return dispatcher != null;
    }

    private void run_dispatcher(){
        dispatcher = Dispatcher.getDispatcher(msgq);
        dispatcher.start();
    }

    public void run(){
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