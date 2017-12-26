package cqjsdk.server;

import cqjsdk.msg.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Receiver extends Thread{

    private DatagramSocket server;
    private Dispatcher dispatcher;
    private boolean toStop;
    private boolean stopped;

    Receiver(DatagramSocket server, Dispatcher dispatcher) {
        this.server = server;
        this.dispatcher = dispatcher;
        toStop = false;
        stopped = false;
    }

    public Boolean stopped(){
        return stopped;
    }

    public void run(){
        byte[] buf = new byte[65536];
        Formatter formatter = Formatter.getFormatter();
        Msg msg;
        while(!toStop){
            DatagramPacket msgpacket = new DatagramPacket(buf, buf.length);
            try {
                server.receive(msgpacket);
                msg = formatter.FormatRecv(msgpacket.getData(), msgpacket.getLength());
                dispatcher.dispatch(msg);
            }
            catch (SocketException | NullPointerException socketEx){
                if(!server.isClosed()){
                    socketEx.printStackTrace();
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        stopped = true;
    }

    public void die(){
        toStop = true;
        server.close();
    }
}