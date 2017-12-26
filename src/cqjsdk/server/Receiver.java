package cqjsdk.server;

import cqjsdk.msg.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver extends Thread{

    private static DatagramSocket server;
    private static Dispatcher dispatcher;
    private static Receiver receiver = new Receiver();

    private Receiver(){}

    static Receiver getReceiver(DatagramSocket server, Dispatcher dispatcher) {
        Receiver.server = server;
        Receiver.dispatcher = dispatcher;
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